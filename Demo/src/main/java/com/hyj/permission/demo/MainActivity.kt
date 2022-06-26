package com.hyj.permission.demo

import android.Manifest.permission.*
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hyj.lib.permission.gps.registerForGpsResult
import com.hyj.lib.permission.launchP
import com.hyj.lib.permission.registerForPermissionResult
import com.hyj.permission.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val singlePermissionLauncher = registerForPermissionResult {
        onGranted { showToast("单权限：同意了权限请求") }
        onDenied { showToast("单权限：拒绝了权限") }
        onPermanentlyDeniedTip { "单权限：运行APP需要这几个权限，请授权。" }
    }

    private val multiPermissionLauncher = registerForPermissionResult {
        onGranted { showToast("多权限：同意了权限请求") }
        onDenied { showToast("多权限：拒绝了权限") }
        onPermanentlyDeniedTip { "多权限：运行APP需要这几个权限，请授权。" }
    }

    private val gpsLauncher = registerForGpsResult {
        onDenied { showToast("拒绝打开GPS") }
        onGranted { showToast("已经打开GPS") }
        onRequestTip { "测试逻辑，测试是否可以准确检查GPS状态！" }
    }

    private val mBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initListener()
    }

    private fun initListener() = mBinding.run {
        btnCheckGPSStatus.setOnClickListener { gpsLauncher.launch() }

        btnSinglePermission.setOnClickListener {
            //"android.permission.NOTIFICATION_SERVICE"
            singlePermissionLauncher.launchP(this@MainActivity, RECORD_AUDIO)
        }
        btnMultiPermission.setOnClickListener {
            val multiplePermission = arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            multiPermissionLauncher.launchP(this@MainActivity, *multiplePermission)
        }
        btnLocationGroup.setOnClickListener {
            val localPermission =
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION)
            multiPermissionLauncher.launchP(this@MainActivity, *localPermission)
        }
        btnBlueTooth.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                showToast("当前版本不是 Android 12 及以上，旧版本的需要定位权限才能进行扫描蓝牙")
            }
            val blueTooth = arrayOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT, BLUETOOTH_ADVERTISE)
            multiPermissionLauncher.launch(blueTooth)
        }
        btnNewStorage.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                showToast("当前版本不是 Android 11 及以上，会自动变更为旧版的请求方式")
            }
            singlePermissionLauncher.launchP(this@MainActivity, MANAGE_EXTERNAL_STORAGE)
        }
        btnInstall.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, REQUEST_INSTALL_PACKAGES)
        }
        btnWindow.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, SYSTEM_ALERT_WINDOW)
        }
        btnSetting.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, WRITE_SETTINGS)
        }
        btnNotification.setOnClickListener {
            //Permission.NOTIFICATION_SERVICE
            singlePermissionLauncher.launchP(
                this@MainActivity,
                "android.permission.NOTIFICATION_SERVICE"
            )
        }
        btnNotificationListener.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, BIND_NOTIFICATION_LISTENER_SERVICE)
        }
        btnPackage.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, PACKAGE_USAGE_STATS)
        }
        btnAlarm.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, SCHEDULE_EXACT_ALARM)
        }
        //TODO 不一样，需要处理
        btnNotDisturb.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity, ACCESS_NOTIFICATION_POLICY)
        }
        //TODO 不一样，需要处理
        btnIgnoreBattery.setOnClickListener {
            singlePermissionLauncher.launchP(
                this@MainActivity, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            )
        }
        btnAppDetail.setOnClickListener {
            singlePermissionLauncher.launchP(this@MainActivity)
        }
    }
}