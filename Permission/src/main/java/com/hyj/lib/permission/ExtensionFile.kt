package com.hyj.lib.permission

import android.app.AlertDialog
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
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
internal fun Context.showPermissionDialog(
    title: CharSequence = "权限申请",
    message: CharSequence = "应用运行需要该权限，请授权！",
    navigationText: CharSequence = "拒绝",
    positiveText: CharSequence = "授权",
    cancel: (() -> Unit),
    confirm: (() -> Unit)
) = AlertDialog.Builder(this).apply {
    setTitle(title)
    setMessage(message)

    setOnCancelListener { cancel() }
    setNegativeButton(navigationText) { _, _ -> cancel() }
    setPositiveButton(positiveText) { _, _ -> confirm() }
}.create().show()