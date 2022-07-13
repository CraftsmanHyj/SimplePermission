package com.hyj.lib.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.hyj.lib.permission.AndroidVersion.isAndroid10
import com.hyj.lib.permission.AndroidVersion.isAndroid11
import com.hyj.lib.permission.AndroidVersion.isAndroid12
import com.hyj.lib.permission.AndroidVersion.isAndroid5
import com.hyj.lib.permission.AndroidVersion.isAndroid5_1
import com.hyj.lib.permission.AndroidVersion.isAndroid6
import com.hyj.lib.permission.AndroidVersion.isAndroid8
import com.hyj.lib.permission.PermissionApi.containsSpecialPermission

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2020/08/18
 * desc   : 权限设置页
 */
internal object PermissionPageIntent {
    /**
     * 根据传入的权限自动选择最合适的权限设置页
     * @param permissions 请求的权限
     */
    fun getSmartPermissionIntent(context: Context, permissions: Array<String>): Intent {
        // 如果失败的权限里面不包含特殊权限
        if (permissions.isEmpty() || !containsSpecialPermission(permissions)) {
            return getApplicationDetailsIntent(context)
        }

        if (isAndroid11 && permissions.size == 3
            && permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)
            && permissions.contains(Permission.READ_EXTERNAL_STORAGE)
            && permissions.contains(Permission.WRITE_EXTERNAL_STORAGE)
        ) return getStoragePermissionIntent(context)

        // 如果当前只有一个权限被拒绝了
        if (permissions.size == 1) {
            when (permissions[0]) {
                Permission.MANAGE_EXTERNAL_STORAGE -> return getStoragePermissionIntent(context)
                Permission.REQUEST_INSTALL_PACKAGES -> return getInstallPermissionIntent(context)
                Permission.SYSTEM_ALERT_WINDOW -> return getWindowPermissionIntent(context)
                Permission.WRITE_SETTINGS -> return getSettingPermissionIntent(context)
                Permission.NOTIFICATION_SERVICE -> return getNotifyPermissionIntent(context)
                Permission.PACKAGE_USAGE_STATS -> return getPackagePermissionIntent(context)
                Permission.BIND_NOTIFICATION_LISTENER_SERVICE ->
                    return getNotificationListenerIntent(context)
                Permission.SCHEDULE_EXACT_ALARM -> return getAlarmPermissionIntent(context)
                Permission.ACCESS_NOTIFICATION_POLICY ->
                    return getNotDisturbPermissionIntent(context)
                Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS ->
                    return getIgnoreBatteryPermissionIntent(context)
            }
        }

        return getApplicationDetailsIntent(context)
    }

    /**
     * 获取应用详情界面意图
     */
    fun getApplicationDetailsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(getPackageNameUri(context))
    }

    /**
     * 获取安装权限设置界面意图
     */
    private fun getInstallPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid8) {
            intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取悬浮窗权限设置界面意图
     */
    private fun getWindowPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid6) {
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取通知栏权限设置界面意图
     */
    private fun getNotifyPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid8) {
            intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        }

        return optimizeIntent(context, intent)
    }

    /**
     * 获取通知监听设置界面意图
     */
    private fun getNotificationListenerIntent(context: Context): Intent {
        val intent = if (isAndroid5_1) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        } else {
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }

        return optimizeIntent(context, intent)
    }

    /**
     * 获取系统设置权限界面意图
     */
    private fun getSettingPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid6) {
            intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取存储权限设置界面意图
     */
    private fun getStoragePermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid11) {
            intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取使用统计权限设置界面意图
     */
    private fun getPackagePermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid5) {
            intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            if (isAndroid10) {
                // 经过测试，只有在 Android 10 及以上加包名才有效果
                // 如果在 Android 10 以下加包名会导致无法跳转
                intent.data = getPackageNameUri(context)
            }
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取勿扰模式设置界面意图
     */
    private fun getNotDisturbPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid6) {
            intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取电池优化选项设置界面意图
     */
    private fun getIgnoreBatteryPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid6) {
            intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取闹钟权限设置界面意图
     */
    private fun getAlarmPermissionIntent(context: Context): Intent {
        var intent: Intent? = null
        if (isAndroid12) {
            intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = getPackageNameUri(context)
        }
        return optimizeIntent(context, intent)
    }

    /**
     * 获取包名 Uri 对象
     */
    private fun getPackageNameUri(context: Context): Uri {
        //return Uri.parse("package:" + context.packageName)
        return Uri.fromParts("package", context.packageName, null)
    }

    /**
     * 判断这个意图的 Activity 是否存在
     * 存在则返回该意图，不存在则返回应用详情意图
     */
    private fun optimizeIntent(context: Context, intent: Intent?): Intent {
        return if (intent.exist(context)) intent!! else getApplicationDetailsIntent(context)
    }
}