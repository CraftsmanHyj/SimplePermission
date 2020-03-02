package com.hyj.permission.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyj.lib.gps.SimpleGPSCallback;
import com.hyj.lib.permission.PermissionManager;
import com.hyj.lib.permission.callback.SimplePermissionCallback;

import java.util.List;

/**
 * <pre>
 *     动态权限申请实例类
 * </pre>
 * Author：hyj
 * Date：2019/1/2 22:40
 */
public class PermissionActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_main);

        mContext = this;
        mActivity = this;
    }

    /**
     * 单个权限申请
     *
     * @param view
     */
    public void simplePermission(View view) {
        PermissionManager.requestPermissions(mActivity, new SimplePermissionCallback<Activity>(mActivity) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(mContext, "activity 已经获取了相机权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(mContext, "activity 拒绝使用相机");
            }

            @Override
            public String getPermissionSetMsg(int reqeustCode) {
                return "activity 扫码需要使用相机权限";
            }
        }, PermsEnm.CAMER);
    }

    /**
     * 多个权限申请
     *
     * @param view
     */
    public void multiPermission(View view) {
        PermissionManager.requestPermissions(mActivity, new SimplePermissionCallback<Activity>(mActivity) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(mContext, "activity 允许位置、联系人权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(mContext, "activity 拒绝使用位置、联系人权限");
            }

            @Override
            public String getPermissionSetMsg(int reqeustCode) {
                return "activity 使用位置、联系人权限，监听用户隐私啊";
            }
        }, PermsEnm.LOCATION_CONTACTS);
    }

    /**
     * 打开手机GPS定位
     *
     * @param view
     */
    public void openGpsLocation(View view) {
        PermissionManager.openGPSLocation(mActivity, new SimpleGPSCallback() {
            @Override
            public void grantedGPS() {
                ToastUtils.showToast(mContext, "GPS定位已经打开");
            }

            @Override
            public void denidedGPS() {
                ToastUtils.showToast(mContext, "拒绝开启GPS定位");
            }

            @Override
            public CharSequence requestGPSTip() {
                return "测试GPS打开功能";
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户的允许、拒绝的统一回调
        PermissionManager.onRequestPermissionsResult(mActivity, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从应用权限设置页面返回，可以从这里获取到设置的结果
        PermissionManager.onActivityResult(mActivity, requestCode);
    }
}