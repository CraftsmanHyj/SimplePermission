package com.hyj.permission.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyj.lib.permission.PermissionManager;
import com.hyj.lib.permission.callback.PermissionCallbackImpl;

import java.util.List;

/**
 * <pre>
 *     动态权限申请实例类
 * </pre>
 * Author：hyj
 * Date：2019/1/2 22:40
 */
public class PermissionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_main);
    }

    /**
     * 单个权限申请
     *
     * @param view
     */
    public void simplePermission(View view) {
        PermissionManager.requestPermissions(this, new PermissionCallbackImpl<AppCompatActivity>(this) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(PermissionActivity.this, "activity 已经获取了相机权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(PermissionActivity.this, "activity 拒绝使用相机");
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
        PermissionManager.requestPermissions(this, new PermissionCallbackImpl<AppCompatActivity>(this) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(PermissionActivity.this, "activity 允许位置、联系人权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(PermissionActivity.this, "activity 拒绝使用位置、联系人权限");
            }

            @Override
            public String getPermissionSetMsg(int reqeustCode) {
                return "activity 使用位置、联系人权限，监听用户隐私啊";
            }
        }, PermsEnm.LOCATION_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户的允许、拒绝的统一回调
        PermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从应用权限设置页面返回，可以从这里获取到设置的结果
        if (PermsEnm.CAMER.getRequestCode() == requestCode) {
            ToastUtils.showToast(this, "activity 相机权限 onActivityResult");
        } else if (PermsEnm.LOCATION_CONTACTS.getRequestCode() == requestCode) {
            ToastUtils.showToast(this, "activity 位置、联系人权限 onActivityResult");
        }
    }
}