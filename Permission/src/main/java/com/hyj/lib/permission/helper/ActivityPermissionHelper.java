package com.hyj.lib.permission.helper;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * <pre>
 *     高版本权限处理
 * </pre>
 * Author：hyj
 * Date：2019/1/2 23:16
 */
public class ActivityPermissionHelper extends PermissionHelper {
    public ActivityPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... perms) {
        ActivityCompat.requestPermissions(getHost(), perms, requestCode);
    }

    @Override
    protected boolean shouldShowRequestPermissionRationale(String deniedPermission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), deniedPermission);
    }
}