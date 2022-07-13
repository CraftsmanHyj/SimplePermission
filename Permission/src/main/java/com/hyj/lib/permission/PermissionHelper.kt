package com.hyj.lib.permission

import android.content.ContextWrapper
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
    callBack: PermissionCallback.() -> Unit
): ActivityResultLauncher<Array<String>> {
    val permCallback = PermissionCallback(this)
    permCallback.callBack()

    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        //resultMap中返回的授权结果不准确，需要自己重新判断，
        //比如：android.permission.ACCESS_NOTIFICATION_POLICY，未授权也会返回true
        val lDenied = mutableListOf<String>()//拒绝权限
        val lPermanentlyDenied = mutableListOf<String>()//不在询问权限
        for (item in resultMap) {
            if (!PermissionApi.isGrantedPermission(context(), item.key)) {
                lDenied.add(item.key)
                //查找权限勾选了不再询问，那么所有拒绝的权限都跳转到设置界面去授权
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context(), item.key)) {
                    lPermanentlyDenied.add(item.key)
                }
            }
        }

        if (lDenied.isEmpty()) permCallback.granted()
        else if (lPermanentlyDenied.isNotEmpty()) permCallback.permanentlyDenied(lPermanentlyDenied.toTypedArray())
        else permCallback.denied()
    }
}

/**
 * 发起动态权限请求
 */
fun ActivityResultLauncher<Array<String>>.launchP(
    context: ContextWrapper,
    vararg permissions: String
) {
    try {
        if (permissions.isEmpty()) {
            context.startActivity(PermissionPageIntent.getApplicationDetailsIntent(context))
            return
        }

        var aPermission = permissions as Array<String>
        // 必须要传入正常的权限或者权限组才能申请权限
        PermissionChecker.checkPermissionArgument(aPermission)
        // 检查申请的存储权限是否符合规范
        PermissionChecker.checkStoragePermission(context, aPermission)
        // 检查申请的定位权限是否符合规范
        PermissionChecker.checkLocationPermission(context, aPermission)
        // 检查申请的权限和 targetSdk 版本是否能吻合
        PermissionChecker.checkTargetSdkVersion(context, permissions)
        // 检测权限有没有在清单文件中注册
        PermissionChecker.checkManifestPermissions(context, aPermission)
        // 优化所申请的权限列表
        aPermission = PermissionChecker.optimizeDeprecatedPermission(aPermission)

        launch(aPermission)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}