package com.hyj.lib.permission

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * 这里接收从launcher传入进来的权限，用于返回之后再验证是否已经给了这个权限
 * 跳转应用设置页的协定
 * User: hyj
 * Date: 2022/6/10 16:45
 */
class LaunchAppSettingContract : ActivityResultContract<Array<String>, Array<String>>() {
    private var requestPerms: Array<String>? = null

    override fun createIntent(context: Context, input: Array<String>): Intent {
        requestPerms = input
        return PermissionPageIntent.getSmartPermissionIntent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Array<String>? = requestPerms
}