package com.hyj.lib.permission.gps

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

/**
 * 单个权限的协定，传入权限字符串，返回Pair<权限，授权结果>
 * User: hyj
 * Date: 2022/6/9 15:38
 */
class GPSSwitchContract : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {}
}