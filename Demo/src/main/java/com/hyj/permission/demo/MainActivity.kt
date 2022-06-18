package com.hyj.permission.demo

import android.Manifest
import android.Manifest.permission.READ_CALENDAR
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hyj.lib.permission.gps.registerForGpsResult
import com.hyj.lib.permission.launchP
import com.hyj.lib.permission.registerForPermissionResult
import com.hyj.permission.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val singlePermissionLauncher = registerForPermissionResult<String> {
        onGranted { showToast("单权限：同意了权限请求") }
        onDenied { showToast("单权限：拒绝了权限") }
        onPermanentlyDeniedTip { "单权限：运行APP需要这几个权限，请授权。" }
    }

    private val multiPersmissionLauncher = registerForPermissionResult<Array<String>> {
        onGranted { showToast("多权限：同意了权限请求") }
        onDenied { showToast("多权限：拒绝了权限") }
        onPermanentlyDeniedTip { "多权限：运行APP需要这几个权限，请授权。" }
    }

    private val gpsLauncher = registerForGpsResult {
        onDenied { showToast("拒绝打开GPS") }
        onGranted { showToast("已经打开GPS") }
        onRequestTip { "测试逻辑，测试是否可以准确检查GPS状态！" }
    }

    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initListener()
    }

    private fun initListener() {
        mBinding.btnSinglePermission.setOnClickListener {
            singlePermissionLauncher.launchP(READ_CALENDAR)
        }

        mBinding.btnMultiPermission.setOnClickListener {
            val multiplePermission = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            multiPersmissionLauncher.launchP(*multiplePermission)
        }

        mBinding.btnCheckGPSStatus.setOnClickListener {
            gpsLauncher.launch()
        }
    }
}