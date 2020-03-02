package com.hyj.lib.gps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.hyj.lib.permission.bean.PermConstant;
import com.hyj.lib.permission.dialog.AppSettingDialog;
import com.hyj.lib.permission.utils.PermUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     手机GPS定位工具类
 *     使用方法
 *     1.调用setGpsResultAction设置回调监听
 *     2.在onActivityResult中注册返回结果处理方法gpsHelper.onActivityResult()
 *     3.gpsHelper.checkGPS()触发gps状态检查
 * </pre>
 * Author：hyj
 * Date：2020/2/3 17:33
 */
public class GPSHelper {
    /**
     * GPS设置请求码
     */
    private static final int REQEUST_CODE_GPS = PermConstant.getReqeustCode();

    /**
     * 用于存放权限回调
     */
    private static final Map<String, IGPSCallBack> mCallBack = new HashMap<>();

    /**
     * 注册 onActivityResult回调方法
     *
     * @param context
     * @param requestCode
     */
    public static void onActivityResult(Context context, int requestCode) {
        String callBackKey = PermUtils.generateCallBackKey(context, requestCode);
        IGPSCallBack callback = mCallBack.get(callBackKey);
        if (null == callback || REQEUST_CODE_GPS != requestCode) {
            return;
        }

        if (checkGpsStatus(context)) {
            callback.grantedGPS();
        } else {
            callback.denidedGPS();
        }

        mCallBack.remove(callBackKey);
    }

    /**
     * 检查GPS是否已经开启
     *
     * @param cxt
     * @return true:已经打开定位；false:没有打开定位
     */
    public static boolean checkGpsStatus(Context cxt) {
        //9.0以下不需要打开GPS,9.0部分需要，10都需要
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            return true;
        }

        // 此处还需要测试看是选择哪种模式，需要根据不同系统来校验
        LocationManager locationManager = (LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检测用户GPS的开关是否有打开
     */
    public static <T> void openGPSLocation(final T actFmg, final IGPSCallBack callBack) {
        if (!(actFmg instanceof Activity) && !(actFmg instanceof Fragment)) {
            throw new IllegalArgumentException("argument must be instanceof android.app.Activity or android.support.v4.app.Fragment");
        }

        if (null == callBack) {
            throw new NullPointerException("GPSCallBack is null");
        }

        Activity act = PermUtils.getActivity(actFmg);
        if (checkGpsStatus(act)) {    //已经开启了GPS
            callBack.grantedGPS();
            return;
        }

        mCallBack.put(PermUtils.generateCallBackKey(act, REQEUST_CODE_GPS), callBack);

        AppSettingDialog.Builder(act)
                .setMessage(callBack.requestGPSTip())
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        if (actFmg instanceof Activity) {
                            ((Activity) actFmg).startActivityForResult(intent, REQEUST_CODE_GPS);
                        } else if (actFmg instanceof Fragment) {
                            ((Fragment) actFmg).startActivityForResult(intent, REQEUST_CODE_GPS);
                        }
                    }
                })
                .setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.denidedGPS();
                    }
                })
                .build()
                .show();
    }
}