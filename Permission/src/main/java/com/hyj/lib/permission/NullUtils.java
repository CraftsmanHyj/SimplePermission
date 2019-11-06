package com.hyj.lib.permission;

import android.app.Activity;

import com.hyj.lib.permission.bean.IPermissionInfo;

/**
 * <pre>
 * </pre>
 * Author：hyj
 * Date：2019/11/6 22:53
 */
class NullUtils {
    /**
     * 检查Activity是否为Null
     *
     * @param activity
     */
    static void checkActivity(Activity activity) {
        if (null == activity) {
            throw new NullPointerException("Activity is null");
        }
    }

    /**
     * 检查IPermissionInfo是否为Null
     *
     * @param permissionInfo
     */
    static void checkIPermInfo(IPermissionInfo permissionInfo) {
        if (null == permissionInfo) {
            throw new NullPointerException("PermissionInfo is null");
        }
    }

    /**
     * 检查是否有需要检查的权限
     *
     * @param perms
     */
    static void checkPermissioins(String... perms) {
        if (null == perms || perms.length <= 0) {
            throw new NullPointerException("Permissions is null");
        }
    }
}
