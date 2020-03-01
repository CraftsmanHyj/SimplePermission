package com.hyj.permission.demo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyj.lib.permission.helper.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     使用原始方式动态请求权限
 * </pre>
 * Author：hyj
 * Date：2019/2/10 21:00
 */
public class PermissionNativeFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_fragment, container, false);
        myInit(view);
        return view;
    }

    private void myInit(View view) {
        view.findViewById(R.id.fragmentSimplePermission).setOnClickListener(this);
        view.findViewById(R.id.fragmentMultiPermission).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragmentSimplePermission:
                fragmentSimplePermission();
                break;

            case R.id.fragmentMultiPermission:
                fragmentMultiPermission();
                break;
        }
    }

    /**
     * 单个权限申请
     */
    private void fragmentSimplePermission() {
        int flag = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (flag != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PermsEnm.CAMER.getPermissions(), PermsEnm.CAMER.getRequestCode());
        }
    }

    /**
     * 多个权限申请
     */
    private void fragmentMultiPermission() {
        int flag = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        if (flag != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PermsEnm.LOCATION_CONTACTS.getPermissions(), PermsEnm.LOCATION_CONTACTS.getRequestCode());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

        callBcakMethod(getActivity(), requestCode, granted, denied);
    }

    private void callBcakMethod(Activity activity, int requestCode, List<String> granted, List<String> denied) {
        if (!denied.isEmpty()) {     //被拒绝的授权
            if (somePermissionPermanetlyDenied(activity, denied)) {     //勾选了不再询问
                AppSettingDialog.<Fragment>Builder(this)
                        .setNegativeListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtils.showToast(getContext(), "native fragment 权限被拒绝");
                            }
                        })
                        .setReqeustCode(requestCode)
                        .setMessage("native fragment 权限被拒绝之后弹出的二次询问框")
                        .build()
                        .show();
            } else {    //拒绝授权
                ToastUtils.showToast(getContext(), "native fragment 权限被拒绝");
            }
        }

        //全部通过了
        if (!granted.isEmpty() && denied.isEmpty()) {
            ToastUtils.showToast(getContext(), "native fragment 权限全部通过");
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param activity          当前activity
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true,反之false
     */
    private boolean somePermissionPermanetlyDenied(Activity activity, List<String> deniedPermissions) {
        return PermissionHelper.newInstance(activity).somePermissionPermanetlyDenied(deniedPermissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从应用权限设置页面返回，可以从这里获取到设置的结果
        if (PermsEnm.CAMER.getRequestCode() == requestCode) {
            ToastUtils.showToast(getContext(), "native fragment 相机权限 onActivityResult");
        } else if (PermsEnm.LOCATION_CONTACTS.getRequestCode() == requestCode) {
            ToastUtils.showToast(getContext(), "native fragment 位置、联系人权限 onActivityResult");
        }
    }
}