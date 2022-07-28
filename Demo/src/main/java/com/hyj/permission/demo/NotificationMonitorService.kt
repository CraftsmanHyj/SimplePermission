package com.hyj.permission.demo

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi

/**
 * User: hyj
 * Date: 2022/7/28 15:14
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationMonitorService : NotificationListenerService() {
    /**
     * 当系统收到新的通知后出发回调
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val extras = sbn!!.notification.extras
            if (extras != null) {
                //获取通知消息标题
                val title = extras.getString(Notification.EXTRA_TITLE)
                // 获取通知消息内容
                val msgText: Any? = extras.getCharSequence(Notification.EXTRA_TEXT)
                showToast("监听到新的通知消息，标题为：$title，内容为：$msgText")
            }
        }
    }

    /**
     * 当系统通知被删掉后出发回调
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        showToast("删除了通知：${sbn!!.id}")
    }
}