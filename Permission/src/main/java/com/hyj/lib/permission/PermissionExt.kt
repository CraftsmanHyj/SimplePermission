package com.hyj.lib.permission

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

/**
 * User: hyj
 * Date: 2022/6/9 16:23
 */

/**
 *TODO
 * 当已经允许的权限拒绝后重启问题：https://blog.csdn.net/suyimin2010/article/details/83445603
 */

/**
 * 发起动态权限请求
 */
fun ActivityResultLauncher<Array<String>>.launchP(vararg permissions: String) {
    this.launch(permissions as Array<String>)
}

/**
 * 生成动态权限申请Launcher
 * 泛型T仅支持：String、Array<String>两种类型
 * @param callBack 权限处理回调接口
 */
inline fun <reified T> ActivityResultCaller.registerForPermissionResult(
    callBack: IPermissionCallbackImpl<T>.() -> Unit
): ActivityResultLauncher<Array<String>> {
    val placeholderValue = when { //根据当前类型创建一个占位符，是否可以优化
        "a" is T -> "a"
        arrayOf("a") is T -> arrayOf("a")
        else -> throw IllegalArgumentException("当前方法中的泛型仅支持String、Array<String>两种类型，请检查传入的类型！")
    } as T

    val permCallback = IPermissionCallbackImpl(this, placeholderValue)
    permCallback.callBack() //调用dsl方法

    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        if (!resultMap.containsValue(false)) {
            permCallback.granted(convertDataType(resultMap.keys.toList()))
            return@registerForActivityResult
        }

        // 获得未授权权限列表、第一次拒绝列表
        val lDenied = mutableListOf<String>()
        for (item in resultMap) if (!item.value) lDenied.add(item.key)

        //查找权限勾选了不再询问，那么所有拒绝的权限都跳转到设置界面去授权
        val permanentlyDenied =
            lDenied.find { !ActivityCompat.shouldShowRequestPermissionRationale(context(), it) }

        if (permanentlyDenied.isNullOrBlank()) {
            permCallback.denied(convertDataType(lDenied))
        } else {
            permCallback.permanentlyDenied(convertDataType(lDenied))
        }
    }
}

/**
 * 类型转换
 */
inline fun <reified T> convertDataType(permissions: List<String>): T {
    if (permissions.isEmpty()) {
        return null as T
    }

    return when {
        "a" is T -> permissions[0]
        arrayOf("a") is T -> permissions.toTypedArray()
        else -> throw IllegalArgumentException("当前方法中的泛型仅支持String、Array<String>两种类型，请检查传入的类型！")
    } as T
}