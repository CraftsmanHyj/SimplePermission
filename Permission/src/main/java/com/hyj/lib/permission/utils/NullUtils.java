package com.hyj.lib.permission.utils;

import android.app.Activity;

import com.hyj.lib.permission.bean.IPermissionInfo;

/**
 * <pre>
 *     数据Null检测，工具类
 * </pre>
 * Author：hyj
 * Date：2019/11/6 22:53
 */
public class NullUtils {
    /**
     * 检查Activity是否为Null
     *
     * @param activity
     */
    public static void checkActivity(Activity activity) {
        if (null == activity) {
            throw new NullPointerException("Activity is null");
        }
    }

    /**
     * 检查IPermissionInfo是否为Null
     *
     * @param permissionInfo
     */
    public static void checkIPermInfo(IPermissionInfo permissionInfo) {
        if (null == permissionInfo) {
            throw new NullPointerException("PermissionInfo is null");
        }
    }

    /**
     * 检查是否有需要检查的权限
     *
     * @param perms
     */
    public static void checkPermissioins(String... perms) {
        if (null == perms || perms.length <= 0) {
            throw new NullPointerException("Permissions is null");
        }
    }
}