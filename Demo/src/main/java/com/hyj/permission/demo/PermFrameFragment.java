package com.hyj.permission.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyj.lib.permission.PermissionManager;
import com.hyj.lib.permission.callback.PermissionCallbackImpl;

import java.util.List;

/**
 * <pre>
 *     使用权限框架请求权限
 * </pre>
 * Author：hyj
 * Date：2019/2/10 21:00
 */
public class PermFrameFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_frame_fragment, container, false);
        myInit(view);
        return view;
    }

    private void myInit(View view) {
        view.findViewById(R.id.frameSimplePermission).setOnClickListener(this);
        view.findViewById(R.id.frameMultiPermission).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frameSimplePermission:
                fragmentSimplePermission();
                break;

            case R.id.frameMultiPermission:
                fragmentMultiPermission();
                break;
        }
    }

    /**
     * 单个权限申请
     */
    private void fragmentSimplePermission() {
        PermissionManager.requestPermissions(getActivity(), new PermissionCallbackImpl<Fragment>(this) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(getContext(), "frame fragment 已经获取了相机权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(getContext(), "frame fragment 拒绝使用相机");
            }

            @Override
            public String getPermissionSetMsg(int reqeustCode) {
                return "frame fragment 扫码需要使用相机权限";
            }
        }, PermsEnm.CAMER);
    }

    /**
     * 多个权限申请
     */
    private void fragmentMultiPermission() {
        PermissionManager.requestPermissions(getActivity(), new PermissionCallbackImpl<Fragment>(this) {
            @Override
            public void onPermissionGranted(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(getContext(), "frame fragment 允许位置、联系人权限");
            }

            @Override
            public void onPermissionDenied(int reqeustCode, List<String> perms) {
                ToastUtils.showToast(getContext(), "frame fragment 拒绝使用位置、联系人权限");
            }

            @Override
            public String getPermissionSetMsg(int reqeustCode) {
                return "frame fragment 使用位置、联系人权限，监听用户隐私啊";
            }
        }, PermsEnm.LOCATION_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户的允许、拒绝的统一回调
        PermissionManager.onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从应用权限设置页面返回，可以从这里获取到设置的结果
        if (PermsEnm.CAMER.getRequestCode() == requestCode) {
            ToastUtils.showToast(getContext(), "frame fragment 相机权限 onActivityResult");
        } else if (PermsEnm.LOCATION_CONTACTS.getRequestCode() == requestCode) {
            ToastUtils.showToast(getContext(), "frame fragment 位置、联系人权限 onActivityResult");
        }
    }
}