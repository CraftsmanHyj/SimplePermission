package com.hyj.lib.permission.helper;

import android.app.Activity;
import android.os.Build;

import java.util.List;

/**
 * <pre>
 *     抽象辅助类
 * </pre>
 * Author：hyj
 * Date：2019/1/2 23:05
 */
public abstract class PermissionHelper {

    private Activity activity;

    public Activity getHost() {
        return activity;
    }

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static PermissionHelper newInstance(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionHelper(activity);
        }
        return new ActivityPermissionHelper(activity);
    }

    /**
     * 用户申请权限
     *
     * @param requestCode
     * @param perms
     */
    public abstract void requestPermissions(int requestCode, String... perms);

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true,反之false
     */
    public boolean somePermissionPermanetlyDenied(List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!shouldShowRequestPermissionRationale(deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 第一次打开App时， false
     * 上次弹出权限请求点击了拒绝，但没有勾选“不再询问”    true
     * 上次弹出权限请求点击了拒绝，并且勾选了“不再询问”    false
     *
     * @param deniedPermission 被拒绝的权限
     * @return 点击了拒绝，但没有勾选“不再询问”返回true，点击了拒绝，并且勾选了“不再询问”返回false；
     */
    protected abstract boolean shouldShowRequestPermissionRationale(String deniedPermission);
}