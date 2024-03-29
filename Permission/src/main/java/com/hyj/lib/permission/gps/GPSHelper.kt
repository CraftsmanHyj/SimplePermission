package com.hyj.lib.permission.gps

import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import com.hyj.lib.permission.context
import com.hyj.lib.permission.showPermissionDialog

/**
 * 获得GPS开关检查对象
 */
fun ActivityResultCaller.registerForGpsResult(
    callBack: GPSResultLauncher.() -> Unit
): GPSResultLauncher {
    val gpsLauncher = GPSResultLauncher(this)
    gpsLauncher.callBack()
    return gpsLauncher
}

/**
 * GPS开关检测
 */
class GPSResultLauncher(launcherCaller: ActivityResultCaller) {
    private var onGranted: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null

    private val context = launcherCaller.context()
    private var requestTip: CharSequence = "GPS高精位置服务未开启，请打开！"
    private val gpsLauncher =
        launcherCaller.registerForActivityResult(GPSSwitchContract()) {
            if (checkGpsStatus()) {
                granted()
            } else {
                denied()
            }
        }

    /**
     * 同意GPS请求回调
     */
    fun onGranted(method: () -> Unit) {
        onGranted = method
    }

    /**
     * 拒绝GPS请求回调
     */
    fun onDenied(method: () -> Unit) {
        onDenied = method
    }

    /**
     * 请求GPS时的提示语
     */
    fun onRequestTip(method: () -> CharSequence) {
        requestTip = method.invoke()
    }

    private fun granted() = onGranted?.invoke()
    private fun denied() = onDenied?.invoke()

    /**
     * 启动GPS检测
     */
    fun launch() {
        if (checkGpsStatus()) {
            granted()
        } else {
            context.showPermissionDialog(
                message = requestTip,
                cancel = { denied() },
                confirm = { gpsLauncher.launch(null) }
            )
        }
    }

    /**
     * 检查当前GPS是否打开
     */
    private fun checkGpsStatus(): Boolean {
        //9.0以下不需要打开GPS,9.0部分需要，10都需要
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            return true
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}