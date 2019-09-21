package com.hyj.lib.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.hyj.lib.permission.annotation.IPermission;
import com.hyj.lib.permission.callback.PermissionCallback;
import com.hyj.lib.permission.helper.PermissionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
public class PermissionManager {
    /**
     * 用于存放权限回调
     */
    private static final Map<String, PermissionCallback> mCallBack = new HashMap<>();

    /**
     * 检查所请求的权限是否被授予
     *
     * @param activity
     * @param perms
     * @return
     */
    public static boolean hasPermissions(@NonNull Activity activity, String... perms) {
        if (null == activity) {
            throw new IllegalArgumentException("activity is null");
        }

        //低于6.0,无线权限判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            //任意一个权限被拒绝，就返回false;
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 申请一个动态权限
     *
     * @param activity    上下文对象
     * @param callback    权限处理结果回调
     * @param requestCode 权限请求码
     * @param perms       所需要的权限
     */
    public static void requestPermissions(@NonNull Activity activity,
                                          @NonNull PermissionCallback callback,
                                          int requestCode, @NonNull String... perms) {
        mCallBack.put(generateCallBackKey(activity, requestCode), callback);

        //发起请求之前，还要做一次检查
        if (hasPermissions(activity, perms)) {  //全部通过
            notifyHasPermissions(activity, requestCode, perms);
            return;
        }

        //权限申请
        PermissionHelper.newInstance(activity).requestPermissions(requestCode, perms);
        return;
    }

    /**
     * 生成CallBack的key
     *
     * @param activity
     * @param requestCode
     * @return
     */
    private static String generateCallBackKey(@NonNull Activity activity, int requestCode) {
        return activity.getClass().getName() + "." + requestCode;
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
     * 处理权限请求结果方法
     * 如果授予、拒绝任何权限，将通过PermissionCallback回调接收结果
     * 以及运行有@IPermission注解的方法
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
//        annotationMethod(activity, requestCode, granted, denied);
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
        String callBackKey = generateCallBackKey(activity, requestCode);
        PermissionCallback callback = mCallBack.get(callBackKey);
        mCallBack.remove(callBackKey);

        if (null == callback) {
            return;
        }

        if (!denied.isEmpty()) {     //被拒绝的授权
            if (somePermissionPermanetlyDenied(activity, denied)) {     //勾选了不再询问
                callback.onPermissionPermanetlyDenied(requestCode, denied);
            } else {    //拒绝授权
                callback.onPermissionDenied(requestCode, denied);
            }
        }

        //全部通过了
        if (!granted.isEmpty() && denied.isEmpty()) {
            callback.onPermissionGranted(requestCode, granted);
        }
    }

    /**
     * 结合注解的方式动态申请权限
     *
     * @param activity    上下文
     * @param requestCode 权限请求码
     * @param granted     同意的权限
     * @param denied      拒绝的权限
     */
    private static void annotationMethod(Activity activity, int requestCode, List<String> granted, List<String> denied) {
        //做回调，通知Acticity
        if (activity instanceof PermissionCallback) {
            PermissionCallback callback = ((PermissionCallback) activity);

            if (!granted.isEmpty()) {    //含有授权的权限
                callback.onPermissionGranted(requestCode, granted);
            }

            if (!denied.isEmpty()) {     //被拒绝的授权
                if (somePermissionPermanetlyDenied(activity, denied)) {     //勾选了不再询问
                    callback.onPermissionPermanetlyDenied(requestCode, denied);
                } else {    //拒绝授权
                    callback.onPermissionDenied(requestCode, denied);
                }
            }
        }

        //全部通过了
        if (!granted.isEmpty() && denied.isEmpty()) {
            reflectAnnotationMethod(activity, requestCode);
        }
    }

    /**
     * 找到指定Activity中，有IPermission注解的，并且请求标识参数的正确方法
     *
     * @param activity
     * @param requestCode
     */
    private static void reflectAnnotationMethod(Activity activity, int requestCode) {
        //获取类
        Class<? extends Activity> clazz = activity.getClass();
        //获取类的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(IPermission.class)) {    //如果方法是IPermission注解
                IPermission iPermission = method.getAnnotation(IPermission.class);  //获取注解
                //如果注解的值等于请求标识码(两次匹配，避免框架冲突)
                if (iPermission.value() == requestCode) {
                    //严格校验

                    //方法必须是返回void(三次匹配)
                    Type returnType = method.getGenericReturnType();
                    if (!"void".equals(returnType.toString())) {
                        throw new RuntimeException(method.getName() + "方法返回必须是void");
                    }

                    //方法参数(四次匹配)
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length > 0) {
                        throw new RuntimeException(method.getName() + "方法不能带有参数");
                    }

                    try {
                        if (!method.isAccessible()) { //当方法为私有
                            method.setAccessible(true);
                        }
                        method.invoke(activity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param activity          当前activity
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true,反之false
     */
    public static boolean somePermissionPermanetlyDenied(Activity activity, List<String> deniedPermissions) {
        return PermissionHelper.newInstance(activity).somePermissionPermanetlyDenied(deniedPermissions);
    }
}