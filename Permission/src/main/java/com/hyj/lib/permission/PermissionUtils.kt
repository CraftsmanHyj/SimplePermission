package com.hyj.lib.permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.text.TextUtils
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.reflect.InvocationTargetException

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限相关工具类
 */
internal object PermissionUtils {
    /**
     * 获取 Android 属性命名空间
     */
    val androidNamespace: String
        get() = "http://schemas.android.com/apk/res/android"

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    fun getManifestPermissions(context: Context): HashMap<String, Int> {
        val manifestPermissions = HashMap<String, Int>()
        val parser = parseAndroidManifest(context)
        if (parser != null) {
            try {
                do {
                    // 当前节点必须为标签头部
                    if (parser.eventType != XmlResourceParser.START_TAG) {
                        continue
                    }

                    // 当前标签必须为 uses-permission
                    if ("uses-permission" != parser.name) {
                        continue
                    }

                    manifestPermissions[parser.getAttributeValue(androidNamespace, "name")] =
                        parser.getAttributeIntValue(
                            androidNamespace, "maxSdkVersion", Int.MAX_VALUE
                        )
                } while (parser.next() != XmlResourceParser.END_DOCUMENT)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } finally {
                parser.close()
            }
        }

        if (manifestPermissions.isEmpty()) {
            try {
                // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
                // https://github.com/getActivity/XXPermissions/issues/35
                val requestedPermissions = context.packageManager.getPackageInfo(
                    context.packageName, PackageManager.GET_PERMISSIONS
                ).requestedPermissions

                if (requestedPermissions != null) {
                    for (permission in requestedPermissions) {
                        manifestPermissions[permission] = Int.MAX_VALUE
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        return manifestPermissions
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun findApkPathCookie(context: Context): Int {
        val assets = context.assets
        val apkPath = context.applicationInfo.sourceDir
        try {
            // 为什么不直接通过反射 AssetManager.findCookieForPath 方法来判断？因为这个 API 属于反射黑名单，反射执行不了
            // 为什么不直接通过反射 AssetManager.addAssetPathInternal 这个非隐藏的方法来判断？因为这个也反射不了
            val method = assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            return method.invoke(assets, apkPath) as Int
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        // 获取失败
        return 0
    }

    /**
     * 解析清单文件
     */
    fun parseAndroidManifest(context: Context): XmlResourceParser? {
        val cookie = findApkPathCookie(context)
        if (cookie == 0) {  // 如果 cookie 为 0，证明获取失败，直接 return
            return null
        }

        try {
            val parser = context.assets.openXmlResourceParser(cookie, "AndroidManifest.xml")
            do {
                // 当前节点必须为标签头部
                if (parser.eventType != XmlResourceParser.START_TAG) {
                    continue
                }

                if ("manifest" == parser.name) {
                    // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
                    // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
                    if (TextUtils.equals(
                            context.packageName,
                            parser.getAttributeValue(null, "package")
                        )
                    ) return parser
                }
            } while (parser.next() != XmlResourceParser.END_DOCUMENT)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 判断是否适配了分区存储
     */
    fun isScopedStorage(context: Context): Boolean {
        try {
            val metaKey = "ScopedStorage"
            val metaData = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            ).metaData

            if (metaData != null && metaData.containsKey(metaKey)) {
                return java.lang.Boolean.parseBoolean(metaData[metaKey].toString())
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }
}