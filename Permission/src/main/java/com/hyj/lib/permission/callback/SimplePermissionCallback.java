package com.hyj.lib.permission.callback;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.hyj.lib.permission.dialog.AppSettingDialog;
import com.hyj.lib.permission.utils.PermSetPage;
import com.hyj.lib.permission.utils.PermUtils;

import java.util.List;

/**
 * <pre>
 *     权限回调调接口({@link IPermissionCallback})的简单实现
 * </pre>
 * Author：hyj
 * Date：2019/1/6 10:54
 */
public class SimplePermissionCallback<T> implements IPermissionCallback {
    private final T actFmg;

    /**
     * {@link IPermissionCallback}的简单实现
     *
     * @param actFmg 必须是 android.app.Activity 或者 android.support.v4.app.Fragment 的子类
     */
    public SimplePermissionCallback(T actFmg) {
        if (!(actFmg instanceof Activity) && !(actFmg instanceof Fragment)) {
            throw new IllegalArgumentException("argument must be instanceof android.app.Activity or android.support.v4.app.Fragment");
        }

        this.actFmg = actFmg;
    }

    @Override
    public void onPermissionGranted(int reqeustCode, List<String> perms) {
    }

    @Override
    public void onPermissionDenied(int reqeustCode, List<String> perms) {
    }

    @Override
    public void onPermissionPermanetlyDenied(final int reqeustCode, final List<String> perms) {
        final Context mCxt = PermUtils.getActivity(actFmg);
        AppSettingDialog.Builder(mCxt)
                .setMessage(getPermissionSetMsg(reqeustCode))
                .setPositiveButton("设置")
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", mCxt.getPackageName(), null);
//                        intent.setData(uri);    //点击跳转设置
//
//                        if (actFmg instanceof Activity) {
//                            ((Activity) actFmg).startActivityForResult(intent, reqeustCode);
//                        } else if (actFmg instanceof Fragment) {
//                            ((Fragment) actFmg).startActivityForResult(intent, reqeustCode);
//                        }

                        PermSetPage.startSetActivity(actFmg, reqeustCode);
                    }
                })
                .setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPermissionDenied(reqeustCode, perms);
                    }
                })
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