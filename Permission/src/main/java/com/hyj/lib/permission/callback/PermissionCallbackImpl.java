package com.hyj.lib.permission.callback;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.hyj.lib.permission.dialog.AppSettingDialog;

import java.util.List;

/**
 * <pre>
 *     权限回调接口的简单实现
 * </pre>
 * Author：hyj
 * Date：2019/1/6 10:54
 */
public class PermissionCallbackImpl<T> implements PermissionCallback {
    private final T actFmgInstance;

    /**
     * {@link PermissionCallback}的简单实现
     *
     * @param actFmg 必须是Activity或者android.support.v4.app.Fragment的子类
     */
    public PermissionCallbackImpl(T actFmg) {
        if (!(actFmg instanceof Activity) && !(actFmg instanceof Fragment)) {
            throw new IllegalArgumentException("argument must be instanceof Activity or android.support.v4.app.Fragment");
        }

        this.actFmgInstance = actFmg;
    }

    @Override
    public void onPermissionGranted(int reqeustCode, List<String> perms) {
    }

    @Override
    public void onPermissionDenied(int reqeustCode, List<String> perms) {
    }

    @Override
    public void onPermissionPermanetlyDenied(final int reqeustCode, final List<String> perms) {
        AppSettingDialog.Builder(actFmgInstance)
                .setCancelListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPermissionDenied(reqeustCode, perms);
                    }
                })
                .setReqeustCode(reqeustCode)
                .setMessage(getPermissionSetMsg(reqeustCode))
                .build()
                .show();
    }

    /**
     * 授权设置弹框：提示语
     *
     * @param reqeustCode 权限请求码
     * @return
     */
    public String getPermissionSetMsg(int reqeustCode) {
        return null;
    }
}