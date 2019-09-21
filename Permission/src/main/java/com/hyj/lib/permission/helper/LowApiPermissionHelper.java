package com.hyj.lib.permission.helper;

import android.app.Activity;

/**
 * <pre>
 *     低版本权限处理
 * </pre>
 * Author：hyj
 * Date：2019/1/2 23:15
 */
public class LowApiPermissionHelper extends PermissionHelper {
    public LowApiPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... perms) {
        throw new IllegalStateException("低于6.0不需要动态申请权限");
    }

    @Override
    protected boolean shouldShowRequestPermissionRationale(String deniedPermission) {
        return false;
    }
}