package com.hyj.lib.permission

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

/**
 * 关于授权后，又关闭权限，导致应用重启的问题
 * https://blog.csdn.net/suyimin2010/article/details/83445603
 * User: hyj
 * Date: 2022/6/9 16:23
 */

/**
 * 生成动态权限申请Launcher
 * 泛型T仅支持：String、Array<String>两种类型
 * @param callBack 权限处理回调接口
 */
fun ActivityResultCaller.registerForPermissionResult(
    callBack: IPermissionCallbackImpl.() -> Unit
): ActivityResultLauncher<Array<String>> {
    val permCallback = IPermissionCallbackImpl(this)
    permCallback.callBack()

    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        if (!resultMap.containsValue(false)) {
            permCallback.granted()
            return@registerForActivityResult
        }

        // 获得未授权权限列表、第一次拒绝列表
        val aDenied = resultMap.filter { !it.value }.keys.toTypedArray()

        //查找权限勾选了不再询问，那么所有拒绝的权限都跳转到设置界面去授权
        val permanentlyDenied =
            aDenied.find { !ActivityCompat.shouldShowRequestPermissionRationale(context(), it) }

        if (permanentlyDenied.isNullOrBlank()) {
            permCallback.denied()
        } else {
            permCallback.permanentlyDenied(aDenied)
        }
    }
}

/**
 * 发起动态权限请求
 */
fun ActivityResultLauncher<Array<String>>.launchP(vararg permissions: String) {
    this.launch(permissions as Array<String>)
}