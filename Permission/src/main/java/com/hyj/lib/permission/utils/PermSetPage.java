package com.hyj.lib.permission.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;

/**
 * <pre>
 *     跳转到权限设置页面
 * </pre>
 * Author：hyj
 * Date：2020/3/3 22:46
 */
public final class PermSetPage {
    /**
     * 获取手机厂商表示
     */
    private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();

    /**
     * 跳转到应用权限设置页面
     *
     * @param actFmg      上下文对象
     * @param reqeustCode 权限请求码
     */
    public static <T> void startSetActivity(T actFmg, int reqeustCode) {
        Context context = PermUtils.getActivity(actFmg);

        Intent intent = null;
        if (MANUFACTURER.contains("huawei")) {
            intent = huawei(context);
        } else if (MANUFACTURER.contains("xiaomi")) {
            intent = xiaomi(context);
        } else if (MANUFACTURER.contains("oppo")) {
            intent = oppo(context);
        } else if (MANUFACTURER.contains("vivo")) {
            intent = vivo(context);
        } else if (MANUFACTURER.contains("meizu")) {
            intent = meizu(context);
        }

        if (intent == null || !hasIntent(context, intent)) {
            intent = google(context);
        }

        // 如果用户在权限设置界面改动了权限，请求权限 Activity 会被重启，加入这个 Flag 就可以避免
        // 不可以加这个标记，否则startActivityForResult之后，无法在onActivityResult中调用返回结果
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            if (actFmg instanceof Activity) {
                ((Activity) actFmg).startActivityForResult(intent, reqeustCode);
            } else if (actFmg instanceof Fragment) {
                ((Fragment) actFmg).startActivityForResult(intent, reqeustCode);
            }
        } catch (Exception ignored) {
            intent = google(context);
            if (actFmg instanceof Activity) {
                ((Activity) actFmg).startActivityForResult(intent, reqeustCode);
            } else if (actFmg instanceof Fragment) {
                ((Fragment) actFmg).startActivityForResult(intent, reqeustCode);
            }
        }
    }

    /**
     * 跳转到google默认app设置界面
     *
     * @param context
     * @return
     */
    private static Intent google(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static Intent huawei(Context context) {
        Intent intent = new Intent();

        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setComponent(new ComponentName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"));
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (hasIntent(context, intent)) {
            return intent;
        }

        return intent;
    }

    private static Intent xiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setPackage("com.miui.securitycenter");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        return intent;
    }

    private static Intent oppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());

        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.securitypermission", "com.coloros.securitypermission.permission.PermissionGroupsActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.coloros.securitypermission", "com.coloros.securitypermission.permission.PermissionManagerActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }
        return intent;
    }

    private static Intent vivo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());

        // vivo x7 Y67 Y85
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        // vivo Y66 x20 x9
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        // Y85
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        // 跳转会报 java.lang.SecurityException: Permission Denial
        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity");
        if (hasIntent(context, intent)) {
            return intent;
        }

        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private static Intent meizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    /**
     * 判断intent是否可用
     *
     * @param context
     * @param intent
     * @return
     */
    private static boolean hasIntent(Context context, Intent intent) {
        return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }
}