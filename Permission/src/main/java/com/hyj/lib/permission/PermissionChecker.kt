package com.hyj.lib.permission

import android.Manifest
import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Build
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * User: hyj
 * Date: 2022/6/19 14:59
 */
internal object PermissionChecker {
    /**
     * 检查权限是否符合要求
     * 必须要传入正常的权限或者权限组才能申请权限
     */
    @Throws(IllegalArgumentException::class)
    fun checkPermissionArgument(permissions: Array<String>) {
        if (permissions.isEmpty()) throw IllegalArgumentException("The requested permission cannot be empty");

        // 如果是 Android 12L 后面的版本，则不进行检查
        if (Build.VERSION.SDK_INT > AndroidVersion.ANDROID_12_L) {
            return
        }

        /**
         * 在开启代码混淆之后，反射 Permission 类中的字段会得到空的字段数组
         * 这个是因为编译后常量会在代码中直接引用，所以 Permission 常量字段在混淆的时候会被移除掉
         * 在开启代码混淆之后，反射 Permission 类中的字段会得到空的字段数组
         * 这个是因为编译后常量会在代码中直接引用，所以 Permission 常量字段在混淆的时候会被移除掉
         */
        val fields = Permission::class.java.declaredFields
        if (fields.isEmpty()) {
            return
        }

        val allPermissions = mutableListOf<String>()
        for (field in fields) {
            if (String::class.java != field.type) {
                continue
            }

            try {
                allPermissions.add(field[null] as String)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        for (permission in permissions) {
            if (!allPermissions.contains(permission)) {
                // 请不要申请危险权限和特殊权限之外的权限
                throw IllegalArgumentException("The $permission  is not a dangerous permission or special permission, please do not apply dynamically")
            }
        }
    }

    /**
     * 检查存储权限
     *
     * @param requestPermissions 请求的权限组
     */
    fun checkStoragePermission(context: Context, requestPermissions: Array<String>) {
        // 如果请求的权限中没有包含外部存储相关的权限，那么就直接返回
        if (!requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)
            && !requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE)
            && !requestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE)
        ) return

        // 是否适配了分区存储
        val scopedStorage = PermissionUtils.isScopedStorage(context)
        val parser = PermissionUtils.parseAndroidManifest(context) ?: return
        try {
            do {
                // 当前节点必须为标签头部
                if (parser.eventType != XmlResourceParser.START_TAG) {
                    continue
                }

                // 当前标签必须为 application
                if ("application" != parser.name) {
                    continue
                }

                val targetSdkVersion = context.applicationInfo.targetSdkVersion
                val requestLegacyExternalStorage = parser.getAttributeBooleanValue(
                    PermissionUtils.androidNamespace,
                    "requestLegacyExternalStorage", false
                )
                // 如果在已经适配 Android 10 的情况下
                check(
                    !(targetSdkVersion >= AndroidVersion.ANDROID_10
                            && !requestLegacyExternalStorage
                            && (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) || !scopedStorage))
                ) {
                    // 请在清单文件 Application 节点中注册 android:requestLegacyExternalStorage="true" 属性
                    // 否则就算申请了权限，也无法在 Android 10 的设备上正常读写外部存储上的文件
                    // 如果你的项目已经全面适配了分区存储，请在清单文件中注册一个 meta-data 属性
                    // <meta-data android:name="ScopedStorage" android:value="true" /> 来跳过该检查
                    "Please register the android:requestLegacyExternalStorage=\"true\" " +
                            "attribute in the AndroidManifest.xml file, otherwise it will cause incompatibility with the old version"
                }

                // 如果在已经适配 Android 11 的情况下
                require(
                    !(targetSdkVersion >= AndroidVersion.ANDROID_11
                            && !requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)
                            && !scopedStorage)
                ) {
                    // 1. 适配分区存储的特性，并在清单文件Application 节点中注册一个 meta-data 属性
                    // <meta-data android:name="ScopedStorage" android:value="true" />
                    // 2. 如果不想适配分区存储，则需要使用 Permission.MANAGE_EXTERNAL_STORAGE 来申请权限
                    // 上面两种方式需要二选一，否则无法在 Android 11 的设备上正常读写外部存储上的文件
                    // 如果不知道该怎么选择，可以看文档：https://github.com/getActivity/XXPermissions/blob/master/HelpDoc
                    "The storage permission application is abnormal. If you have adapted the scope storage, " +
                            "please register the <meta-data android:name=\"ScopedStorage\" android:value=\"true\" /> attribute in the AndroidManifest.xml file. " +
                            "If there is no adaptation scope storage, please use MANAGE_EXTERNAL_STORAGE to apply for permission"
                }

                // 终止循环
                break
            } while (parser.next() != XmlResourceParser.END_DOCUMENT)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
    }

    /**
     * 检查定位权限
     *
     * @param requestPermissions 请求的权限组
     */
    fun checkLocationPermission(context: Context, requestPermissions: Array<String>) {
        if (context.applicationInfo.targetSdkVersion >= AndroidVersion.ANDROID_12) {
            require(
                !(requestPermissions.contains(Permission.ACCESS_FINE_LOCATION)
                        && !requestPermissions.contains(Permission.ACCESS_COARSE_LOCATION))
            ) {
                // 如果您的应用以 Android 12 为目标平台并且您请求 ACCESS_FINE_LOCATION 权限
                // 则还必须请求 ACCESS_COARSE_LOCATION 权限。您必须在单个运行时请求中包含这两项权限
                // 如果您尝试仅请求 ACCESS_FINE_LOCATION，则系统会忽略该请求并在 Logcat 中记录以下错误消息：
                // ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION
                // 官方适配文档：https://developer.android.google.cn/about/versions/12/approximate-location
                "If your app targets Android 12 or higher " +
                        "and requests the ACCESS_FINE_LOCATION runtime permission, " +
                        "you must also request the ACCESS_COARSE_LOCATION permission. " +
                        "You must include both permissions in a single runtime request."
            }
        }

        // 判断是否包含后台定位权限
        if (!requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        }

        require(
            !(requestPermissions.contains(Permission.ACCESS_COARSE_LOCATION)
                    && !requestPermissions.contains(Permission.ACCESS_FINE_LOCATION))
        ) {
            // 申请后台定位权限可以不包含模糊定位权限，但是一定要包含精确定位权限，否则后台定位权限会无法申请
            // 也就是会导致无法弹出授权弹窗，经过实践，在 Android 12 上这个问题已经被解决了
            // 但是为了兼容 Android 12 以下的设备还是要那么做，否则在 Android 11 及以下设备会出现异常
            "The application for background location permissions must include precise location permissions"
        }

        for (permission in requestPermissions) {
            if (Permission.ACCESS_FINE_LOCATION == permission
                || Permission.ACCESS_COARSE_LOCATION == permission
                || Permission.ACCESS_BACKGROUND_LOCATION == permission
            ) continue

            throw java.lang.IllegalArgumentException("Because it includes background location permissions, do not apply for permissions unrelated to location")
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
     * @param requestPermissions            请求的权限组
     */
    fun checkTargetSdkVersion(context: Context, requestPermissions: Array<String>) {
        // targetSdk 最低版本要求
        val targetSdkMinVersion =
            if (requestPermissions.contains(Permission.BLUETOOTH_SCAN)
                || requestPermissions.contains(Permission.BLUETOOTH_CONNECT)
                || requestPermissions.contains(Permission.BLUETOOTH_ADVERTISE)
                || requestPermissions.contains(Permission.SCHEDULE_EXACT_ALARM)
            ) {
                AndroidVersion.ANDROID_12
            } else if (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 必须设置 targetSdkVersion >= 30 才能正常检测权限，否则请使用 Permission.Group.STORAGE 来申请存储权限
                AndroidVersion.ANDROID_11
            } else if (requestPermissions.contains(Permission.ACCESS_BACKGROUND_LOCATION)
                || requestPermissions.contains(Permission.ACTIVITY_RECOGNITION)
                || requestPermissions.contains(Permission.ACCESS_MEDIA_LOCATION)
            ) {
                AndroidVersion.ANDROID_10
            } else if (requestPermissions.contains(Permission.ACCEPT_HANDOVER)) {
                AndroidVersion.ANDROID_9
            } else if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES)
                || requestPermissions.contains(Permission.ANSWER_PHONE_CALLS)
                || requestPermissions.contains(Permission.READ_PHONE_NUMBERS)
            ) {
                AndroidVersion.ANDROID_8
            } else {
                AndroidVersion.ANDROID_6
            }

        // 必须设置正确的 targetSdkVersion 才能正常检测权限
        if (context.applicationInfo.targetSdkVersion < targetSdkMinVersion) {
            throw RuntimeException(
                "The targetSdkVersion SDK must be " + targetSdkMinVersion +
                        " or more, if you do not want to upgrade targetSdkVersion, " +
                        "please apply with the old permissions"
            )
        }
    }

    /**
     * 检查清单文件中所注册的权限是否正常
     *
     * @param requestPermissions 请求的权限组
     */
    fun checkManifestPermissions(context: Context, requestPermissions: Array<String>) {
        val manifestPermissions = PermissionUtils.getManifestPermissions(context)

        check(manifestPermissions.isNotEmpty()) { "No permissions are registered in the AndroidManifest.xml file" }

        val minSdkVersion =
            if (Build.VERSION.SDK_INT >= AndroidVersion.ANDROID_7)
                context.applicationInfo.minSdkVersion
            else
                AndroidVersion.ANDROID_6

        for (permission in requestPermissions) {
            if (Permission.NOTIFICATION_SERVICE == permission) {
                // 不检测通知栏权限有没有在清单文件中注册，因为这个权限是框架虚拟出来的，有没有在清单文件中注册都没关系
                continue
            }
            if (Permission.BIND_NOTIFICATION_LISTENER_SERVICE == permission) {
                // 不检测通知栏监听权限有没有在清单文件中注册，因为这个权限是需要直接注册在清单文件中的 service 节点上面的
                continue
            }

            if (minSdkVersion < AndroidVersion.ANDROID_12) {
                if (Permission.BLUETOOTH_SCAN == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        AndroidVersion.ANDROID_11
                    )
                    // 这是 Android 12 之前遗留的问题，获取扫描蓝牙的结果需要精确定位权限
                    checkManifestPermission(
                        manifestPermissions,
                        Permission.ACCESS_FINE_LOCATION,
                        AndroidVersion.ANDROID_11
                    )
                }
                if (Permission.BLUETOOTH_CONNECT == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Manifest.permission.BLUETOOTH,
                        AndroidVersion.ANDROID_11
                    )
                }
                if (Permission.BLUETOOTH_ADVERTISE == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        AndroidVersion.ANDROID_11
                    )
                }
            }

            if (minSdkVersion < AndroidVersion.ANDROID_11) {
                if (Permission.MANAGE_EXTERNAL_STORAGE == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Permission.READ_EXTERNAL_STORAGE,
                        AndroidVersion.ANDROID_10
                    )
                    checkManifestPermission(
                        manifestPermissions,
                        Permission.WRITE_EXTERNAL_STORAGE,
                        AndroidVersion.ANDROID_10
                    )
                }
            }

            if (minSdkVersion < AndroidVersion.ANDROID_10) {
                if (Permission.ACTIVITY_RECOGNITION == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Permission.BODY_SENSORS,
                        AndroidVersion.ANDROID_8
                    )
                }
            }

            if (minSdkVersion < AndroidVersion.ANDROID_8) {
                if (Permission.READ_PHONE_NUMBERS == permission) {
                    checkManifestPermission(
                        manifestPermissions,
                        Permission.READ_PHONE_STATE,
                        AndroidVersion.ANDROID_7_1
                    )
                }
            }
            checkManifestPermission(
                manifestPermissions,
                permission,
                Int.MAX_VALUE
            )
        }
    }

    /**
     * 检查某个权限注册是否正常，如果是则会抛出异常
     *
     * @param manifestPermissions       清单权限组
     * @param checkPermission           被检查的权限
     * @param maxSdkVersion             最低要求的 maxSdkVersion
     */
    private fun checkManifestPermission(
        manifestPermissions: HashMap<String, Int>,
        checkPermission: String, maxSdkVersion: Int
    ) {
        check(manifestPermissions.containsKey(checkPermission)) {
            // 动态申请的权限没有在清单文件中注册，分为以下两种情况：
            // 1. 如果你的项目没有在清单文件中注册这个权限，请直接在清单文件中注册一下即可
            // 2. 如果你的项目明明已注册这个权限，可以检查一下编译完成的 apk 包中是否包含该权限，如果里面没有，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" tools:node="remove"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/98
            "Please register permissions in the AndroidManifest.xml file " +
                    "<uses-permission android:name=\"" + checkPermission + "\" />"
        }

        val manifestMaxSdkVersion = manifestPermissions[checkPermission] ?: return
        require(manifestMaxSdkVersion >= maxSdkVersion) {
            // 清单文件中所注册的权限 maxSdkVersion 大小不符合最低要求，分为以下两种情况：
            // 1. 如果你的项目中注册了该属性，请根据报错提示修改 maxSdkVersion 属性值或者删除 maxSdkVersion 属性
            // 2. 如果你明明没有注册过 maxSdkVersion 属性，可以检查一下编译完成的 apk 包中是否有该属性，如果里面存在，证明框架的判断是没有问题的
            //    一般是第三方 sdk 或者框架在清单文件中注册了 <uses-permission android:name="xxx" android:maxSdkVersion="xx"/> 导致的
            //    解决方式也很简单，通过在项目中注册 <uses-permission android:name="xxx" tools:node="replace"/> 即可替换掉原先的配置
            "The AndroidManifest.xml file " +
                    "<uses-permission android:name=\"" + checkPermission +
                    "\" android:maxSdkVersion=\"" + manifestMaxSdkVersion +
                    "\" /> does not meet the requirements, " +
                    if (maxSdkVersion != Int.MAX_VALUE) "the minimum requirement for maxSdkVersion is $maxSdkVersion" else "please delete the android:maxSdkVersion=\"$manifestMaxSdkVersion\" attribute"
        }
    }

    /**
     * 处理和优化已经过时的权限
     *
     * @param reqPerm 请求的权限组
     */
    fun optimizeDeprecatedPermission(reqPerm: Array<String>): Array<String> {
        val requestPermissions = reqPerm.toMutableList()
        // 如果本次申请包含了 Android 12 蓝牙扫描权限
        if (!AndroidVersion.isAndroid12
            && requestPermissions.contains(Permission.BLUETOOTH_SCAN)
            && !requestPermissions.contains(Permission.ACCESS_FINE_LOCATION)
        ) {
            // 这是 Android 12 之前遗留的问题，扫描蓝牙需要精确定位权限
            requestPermissions.add(Permission.ACCESS_FINE_LOCATION)
        }

        // 如果本次申请包含了 Android 11 存储权限
        if (requestPermissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)) {
            require(
                !(requestPermissions.contains(Permission.READ_EXTERNAL_STORAGE) ||
                        requestPermissions.contains(Permission.WRITE_EXTERNAL_STORAGE))
            ) {
                // 检测是否有旧版的存储权限，有的话直接抛出异常，请不要自己动态申请这两个权限
                "If you have applied for MANAGE_EXTERNAL_STORAGE permissions, " +
                        "do not apply for the READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions"
            }
            if (!AndroidVersion.isAndroid11) {
                // 自动添加旧版的存储权限，因为旧版的系统不支持申请新版的存储权限
                requestPermissions.add(Permission.READ_EXTERNAL_STORAGE)
                requestPermissions.add(Permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        if (!AndroidVersion.isAndroid8 &&
            requestPermissions.contains(Permission.READ_PHONE_NUMBERS) &&
            !requestPermissions.contains(Permission.READ_PHONE_STATE)
        ) {
            // 自动添加旧版的读取电话号码权限，因为旧版的系统不支持申请新版的权限
            requestPermissions.add(Permission.READ_PHONE_STATE)
        }
        if (!AndroidVersion.isAndroid10 &&
            requestPermissions.contains(Permission.ACTIVITY_RECOGNITION) &&
            !requestPermissions.contains(Permission.BODY_SENSORS)
        ) {
            // 自动添加传感器权限，因为 ACTIVITY_RECOGNITION 是从 Android 10 开始才从传感器权限中剥离成独立权限
            requestPermissions.add(Permission.BODY_SENSORS)
        }
        return requestPermissions.toTypedArray()
    }
}