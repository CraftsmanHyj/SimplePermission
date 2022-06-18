package com.hyj.lib.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

/**
 * 这里接收从launcher传入进来的权限，用于返回之后再验证是否已经给了这个权限
 * 跳转应用设置页的协定
 * User: hyj
 * Date: 2022/6/10 16:45
 */
class LaunchAppSettingContract<T> : ActivityResultContract<T, T?>() {
    private var permission: T? = null

    override fun createIntent(context: Context, input: T?): Intent {
        permission = input
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", context.packageName, null))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): T? {
        return permission
    }
}