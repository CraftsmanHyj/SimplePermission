package com.hyj.lib.permission

import androidx.activity.result.ActivityResultCaller

/**
 * 权限处理结果回调方法
 * User: hyj
 * Date: 2022/6/9 17:23
 */
interface IPermissionCallback<T> {
    /**
     * 授予授权通过的回调方法
     * @param perms       请求权限组(方便debug,如用户只授权权限组部分权限)
     */
    fun granted(perms: T)

    /**
     * 权限拒绝回调
     * @param perms       请求的权限组
     */
    fun denied(perms: T)

    /**
     * 权限拒绝，且勾选了不再询问回调
     * @param perms
     */
    fun permanentlyDenied(perms: T)
}

class IPermissionCallbackImpl<T>(
    private val launcherCaller: ActivityResultCaller,
    private val placeholder: T
) : IPermissionCallback<T> {
    private var onGranted: ((perms: T) -> Unit)? = null
    private var onDenied: ((perms: T) -> Unit)? = null
    private var onPermanentlyDenied: ((perms: T) -> Unit)? = null

    //“不再询问”后的弹窗提示
    private var permanentlyDeniedTip: String = "应用需要此权限才可以运行，请在设置中授权！"

    //跳转到应用详情设置权限
    private val appSetLauncher = launcherCaller.appSetLauncher(this, placeholder)

    /**
     * 授予授权通过的回调方法
     */
    fun onGranted(method: (perms: T) -> Unit) {
        onGranted = method
    }

    /**
     * 权限拒绝回调
     */
    fun onDenied(method: (perms: T) -> Unit) {
        onDenied = method
    }

    /**
     * 权限拒绝，且勾选了不再询问时的回调
     */
    fun onPermanentlyDenied(method: (perms: T) -> Unit) {
        onPermanentlyDenied = method
    }

    /**
     * 勾选了不再询问时二次弹窗的提示语
     */
    fun onPermanentlyDeniedTip(method: () -> String) {
        permanentlyDeniedTip = method.invoke()
    }

    override fun granted(perms: T) {
        onGranted?.invoke(perms)
    }

    override fun denied(perms: T) {
        onDenied?.invoke(perms)
    }

    override fun permanentlyDenied(perms: T) {
        onPermanentlyDenied?.invoke(perms) ?: showSetDialog(perms)
    }

    private fun showSetDialog(perms: T) {
        launcherCaller.context().showPermissionDialog(
            message = permanentlyDeniedTip,
            cancel = { denied(perms) },
            confirm = { appSetLauncher.launch(perms) }
        )
    }
}