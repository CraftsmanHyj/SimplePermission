package com.hyj.lib.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.hyj.lib.gps.GPSHelper;
import com.hyj.lib.gps.IGPSCallBack;
import com.hyj.lib.permission.bean.IPermissionInfo;
import com.hyj.lib.permission.callback.IPermissionCallback;
import com.hyj.lib.permission.helper.PermissionHelper;
import com.hyj.lib.permission.utils.PermUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     权限管理类
 * </pre>
 * Author：hyj
 * Date：2019/1/2 22:52
 */
public final class PermissionManager {
    /**
     * 用于存放权限回调
     */
    private static final Map<String, IPermissionCallback> mCallBack = new HashMap<>();
    /**
     * 用于存放请求的权限
     */
    private static final Map<String, List<String>> mPerms = new HashMap<>();

    /**
     * 检测用户是否打开GPS定位功能
     *
     * @param actFmg
     * @param callBack
     * @param <T>
     */
    public static <T> void openGPSLocation(final T actFmg, final IGPSCallBack callBack) {
        GPSHelper.openGPSLocation(actFmg, callBack);
    }

    /**
     * 检查所请求的权限是否被授予
     *
     * @param activity 上下文对象
     * @param perms    权限请求信息
     * @return
     */
    private static boolean hasPermissions(@NonNull Activity activity, String... perms) {
        PermUtils.checkActivity(activity);
        PermUtils.checkPermissioins(perms);

        //低于6.0,无线权限判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                //任意一个权限被拒绝，就返回false;
                return false;
            }
        }

        return true;
    }

    /**
     * 申请动态权限
     *
     * @param activity 上下文对象
     * @param callback 权限处理结果回调
     * @param permInfo 权限请求信息
     */
    public static void requestPermissions(@NonNull Activity activity, @NonNull IPermissionCallback callback, IPermissionInfo permInfo) {
        PermUtils.checkIPermInfo(permInfo);
        requestPermissions(activity, callback, permInfo.getRequestCode(), permInfo.getPermissions());
    }

    /**
     * <pre>
     *     申请动态权限
     *     此方法将在后期版本中删除，请调用 requestPermissions(Activity, IPermissionCallback, IPermissionInfo)
     * </pre>
     *
     * @param activity    上下文对象
     * @param callback    权限处理结果回调
     * @param requestCode 权限请求码
     * @param perms       权限请求信息
     */
    @Deprecated
    public static void requestPermissions(@NonNull Activity activity, @NonNull IPermissionCallback callback, int requestCode, String... perms) {
        PermUtils.checkActivity(activity);
        PermUtils.checkPermissioins(perms);

        String key = PermUtils.generateCallBackKey(activity, requestCode);
        mCallBack.put(key, callback);

        //发起请求之前，还要做一次检查
        if (hasPermissions(activity, perms)) {  //全部通过
            notifyHasPermissions(activity, requestCode, perms);
            return;
        }

        //权限申请
        PermissionHelper.newInstance(activity).requestPermissions(requestCode, perms);
    }

    /**
     * 如果全部已被授权则进入onReqeustPermissionResult方法返回结果
     *
     * @param activity
     * @param requestCode
     * @param perms
     */
    private static void notifyHasPermissions(Activity activity, int requestCode, String... perms) {
        //将授权通过的权限组转参告知处理权限结果方法
        int[] grantResults = new int[perms.length];
        for (int i = 0, len = grantResults.length; i < len; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;    //全部通过
        }
        onRequestPermissionsResult(activity, requestCode, perms, grantResults);
    }

    /**
     * <pre>
     *     处理权限请求结果方法
     *     如果授予、拒绝任何权限，将通过PermissionCallback回调接收结果
     *     以及运行有@IPermission注解的方法
     * </pre>
     *
     * @param activity     拥有实现PermissionCallback接口或有@IPermission注解的Activity
     * @param requestCode  回调请求码
     * @param permissions  回调权限组
     * @param grantResults 回调授权结果
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        List<String> granted = new ArrayList<>();//已经被授权的权限
        List<String> denied = new ArrayList<>();//已经被拒绝的权限
        //分类
        for (int i = 0, len = permissions.length; i < len; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                granted.add(permissions[i]);
            } else {
                denied.add(permissions[i]);
            }
        }

        callBcakMethod(activity, requestCode, granted, denied);
    }

    /**
     * 使用回调的方式来进行动态权限判定
     *
     * @param activity    上下文
     * @param requestCode 权限请求码
     * @param granted     同意的权限
     * @param denied      拒绝的权限
     */
    private static void callBcakMethod(Activity activity, int requestCode, List<String> granted, List<String> denied) {
        String callBackKey = PermUtils.generateCallBackKey(activity, requestCode);
        IPermissionCallback callback = mCallBack.get(callBackKey);
        if (null == callback) {
            return;
        }

        if (!denied.isEmpty()) {     //被拒绝的授权
            if (somePermissionPermanetlyDenied(activity, denied)) {     //勾选了不再询问
                List<String> lPerms = new ArrayList<>();
                lPerms.addAll(granted);
                lPerms.addAll(denied);
                mPerms.put(callBackKey, lPerms);

                callback.onPermissionPermanetlyDenied(requestCode, denied);
            } else {    //拒绝授权
                mCallBack.remove(callBackKey);
                callback.onPermissionDenied(requestCode, denied);
            }
        }

        //全部通过了
        if (!granted.isEmpty() && denied.isEmpty()) {
            mCallBack.remove(callBackKey);
            callback.onPermissionGranted(requestCode, granted);
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param activity          当前activity
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true,反之false
     */
    private static boolean somePermissionPermanetlyDenied(Activity activity, List<String> deniedPermissions) {
        return PermissionHelper.newInstance(activity).somePermissionPermanetlyDenied(deniedPermissions);
    }

    /**
     * <pre>
     *     处理拒绝且不再询问之后，进入应用权限设置页面设置后
     *     返回App的页面，接收返回结果的处理
     * </pre>
     *
     * @param activity
     * @param requestCode
     */
    public static void onActivityResult(Activity activity, int requestCode) {
        GPSHelper.onActivityResult(activity, requestCode);

        String callBackKey = PermUtils.generateCallBackKey(activity, requestCode);
        IPermissionCallback callback = mCallBack.get(callBackKey);
        if (null == callback) {
            return;
        }

        List<String> granted = new ArrayList<>();//已经被授权的权限
        List<String> denied = new ArrayList<>();//已经被拒绝的权限

        List<String> perms = mPerms.get(callBackKey);
        for (String perm : perms) {
            if (hasPermissions(activity, perm)) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        if (!denied.isEmpty()) {     //被拒绝的授权
            callback.onPermissionDenied(requestCode, denied);
        }

        if (!granted.isEmpty() && denied.isEmpty()) {   //全部通过了
            callback.onPermissionGranted(requestCode, granted);
        }

        mCallBack.remove(callBackKey);
        mPerms.remove(callback);
    }
}