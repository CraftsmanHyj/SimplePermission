# SimplePermission
处理需要动态申请的用户权限：

+ 对于拒绝、不再询问有做再次调起的逻辑封装，支持国产定制系统。
+ 支持一次申请单个、多个动态权限；
+ 支持拒绝后弹窗询问再次请求及提示语自定义；



[更新日志]( https://github.com/CraftsmanHyj/SimplePermission/blob/master/docs/UpdateLog.md )；　　[动态权限列表](https://github.com/CraftsmanHyj/SimplePermission/blob/master/docs/%E6%9D%83%E9%99%90%E8%AF%B4%E6%98%8E.md)；　　[下载Demo体验效果](https://github.com/CraftsmanHyj/SimplePermission/raw/master/docs/Demo.apk)

# 添加依赖

**Step 1.** 在项目的根目录`build.gradle`文件中加入JitPack仓库

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** 在当前APP`Demo/build.gradle`文件中加入库的依赖

version：[![](https://jitpack.io/v/CraftsmanHyj/SimplePermission.svg)](https://jitpack.io/#CraftsmanHyj/SimplePermission)

```groovy
dependencies {
    implementation 'com.github.CraftsmanHyj:SimplePermission:${version}'
}
```



# 使用示例

```kotlin
class MainActivity : AppCompatActivity() {
    private val gpsLauncher = registerForGpsResult {
        onDenied { showToast("拒绝打开GPS") }
        onGranted { showToast("已经打开GPS") }
        onRequestTip { "测试逻辑，测试是否可以准确检查GPS状态！" }
    }
	
    //需要在lifeCycleOwner start状态前创建Register对象
    private val multiPermissionLauncher = registerForPermissionResult {
        onGranted { showToast("多权限：同意了权限请求") }
        onDenied { showToast("多权限：拒绝了权限") }
        onPermanentlyDeniedTip { "多权限：运行APP需要这几个权限，请授权。" }
    }
	
	......

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initListener()
    }

    private fun initListener() = mBinding.run {
        btnCheckGPSStatus.setOnClickListener { gpsLauncher.launch() }

        btnMultiPermission.setOnClickListener {
            val multiplePermission = arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            multiPermissionLauncher.launchP(this@MainActivity, *multiplePermission)
        }
		
		......
    }
}
```



优秀权限库：[XXPermissions](https://github.com/getActivity/XXPermissions)