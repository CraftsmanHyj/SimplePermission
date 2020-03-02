package com.hyj.lib.permission.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.hyj.lib.permission.bean.IPermissionInfo;

/**
 * <pre>
 *     工具类
 * </pre>
 * Author：hyj
 * Date：2019/11/6 22:53
 */
public final class PermUtils {
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

    /**
     * 生成CallBack的key
     *
     * @param context
     * @param requestCode
     * @return
     */
    public static String generateCallBackKey(@NonNull Context context, int requestCode) {
        return context.getClass().getName() + "." + requestCode;
    }

    /**
     * 强转，获取Activity对象
     *
     * @param t
     * @param <T> t extends Activity
     * @return Activity
     */
    public static <T> Activity getActivity(T t) {
        if (t instanceof Activity) {
            return (Activity) t;
        } else if (t instanceof Fragment) {
            return ((Fragment) t).getActivity();
        } else {
            throw new IllegalArgumentException("argument must be instanceof Activity or android.support.v4.app.Fragment");
        }
    }
}