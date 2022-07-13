package com.hyj.lib.permission

import androidx.activity.result.ActivityResultCaller

/**
 * 权限回调接口
 */
class PermissionCallback(private val launcherCaller: ActivityResultCaller) {
    private var onGranted: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null
    private var onPermanentlyDenied: ((Array<String>) -> Unit)? = null

    //“不再询问”后的弹窗提示
    private var permanentlyDeniedTip: String = "应用需要此权限才可以运行，请在应用信息的权限管理中授权！"

    //跳转到应用详情设置权限
    private val appSetLauncher =
        launcherCaller.registerForActivityResult(LaunchAppSettingContract()) { permissions ->
            if (PermissionApi.isGrantedPermissions(launcherCaller.context(), permissions)) {
                granted()
            } else {
                denied()
            }
        }

    /**
     * 授予授权通过的回调方法
     */
    fun onGranted(method: () -> Unit) {
        onGranted = method
    }

    /**
     * 权限拒绝回调
     */
    fun onDenied(method: () -> Unit) {
        onDenied = method
    }

    /**
     * 权限拒绝，且勾选了不再询问时的回调
     */
    fun onPermanentlyDenied(method: (Array<String>) -> Unit) {
        onPermanentlyDenied = method
    }

    /**
     * 勾选了不再询问时二次弹窗的提示语
     */
    fun onPermanentlyDeniedTip(method: () -> String) {
        permanentlyDeniedTip = method.invoke()
    }

    internal fun granted() {
        onGranted?.invoke()
    }

    internal fun denied() {
        onDenied?.invoke()
    }

    internal fun permanentlyDenied(deniedPermission: Array<String>) {
        onPermanentlyDenied?.invoke(deniedPermission) ?: showSetDialog(deniedPermission)
    }

    private fun showSetDialog(deniedPermission: Array<String>) {
        launcherCaller.context().showPermissionDialog(
            message = permanentlyDeniedTip,
            cancel = { denied() },
            confirm = { appSetLauncher.launch(deniedPermission) }
        )
    }
}