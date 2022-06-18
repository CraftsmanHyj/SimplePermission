package com.hyj.lib.permission

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 内部使用的扩展函数
 * User: hyj
 * Date: 2022/6/9 17:48
 */

/**
 * 获取上下文
 * */
fun ActivityResultCaller.context() =
    if (this is ComponentActivity) this else (this as Fragment).requireActivity()

/**
 * 应用授权弹窗提示
 */
fun Context.showPermissionDialog(
    title: CharSequence = "权限申请",
    message: CharSequence? = null,
    cancel: (() -> Unit),
    confirm: (() -> Unit)
) = AlertDialog.Builder(this).apply {
    setTitle(title)
    setMessage(if (message.isNullOrBlank()) "应用运行需要该权限，请授权！" else message)

    setOnCancelListener { cancel() }
    setNegativeButton("拒绝") { _, _ -> cancel() }
    setPositiveButton("授权") { _, _ -> confirm() }
}.create().show()

/**
 * 跳转应用设置页的Launcher
 * */
fun <T> ActivityResultCaller.appSetLauncher(
    callBack: IPermissionCallback<T>,
    placeholder: T
) = registerForActivityResult(LaunchAppSettingsContract<T?>()) { permissions ->
    if (placeholder is String) {
        val hasDenied = PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
            context(),
            permissions as String
        )

        if (hasDenied) {
            callBack.denied(permissions as T)
        } else {
            callBack.granted(permissions as T)
        }
    } else {
        val perms = permissions as Array<String?>?
        val denied = perms?.find {
            PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context(), it!!)
        }

        if (!denied.isNullOrBlank()) {
            callBack.denied(permissions as T)
        } else {
            callBack.granted(permissions as T)
        }
    }
}