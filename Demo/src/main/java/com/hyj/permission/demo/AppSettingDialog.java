package com.hyj.permission.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * <pre>
 * </pre>
 * Author：hyj
 * Date：2020/3/1 21:10
 */
public class AppSettingDialog<T> implements DialogInterface.OnClickListener {
    /**
     * 跳转到应用设置界面的默认请求码
     */
    public static final int PERMISSION_SETTING_CODE = 12345;

    /**
     * 非法参数异常
     */
    private static final IllegalArgumentException illegalException =
            new IllegalArgumentException("argument must be instanceof Activity or android.support.v4.app.Fragment");

    private final T ACTIVITY;//activity或fragment对象
    private final String TITLE;//对话框标题
    private final String MESSAGE;//解释为什么需要这组权限的提示内容
    private final String POSITIVEBUTTON;//确定按钮
    private final String NEGATIVEBUTTON;//取消按钮
    private final DialogInterface.OnClickListener CANCELLISTENER;//对话框取消按钮监听
    private final int REQEUSTCODE;//请求标识码

    private AppSettingDialog(Builder buildler) {
        this.ACTIVITY = (T) buildler.activity;
        this.TITLE = buildler.title;
        this.MESSAGE = buildler.message;
        this.POSITIVEBUTTON = buildler.positiveButton;
        this.NEGATIVEBUTTON = buildler.negativeButton;
        this.CANCELLISTENER = buildler.negativeListener;
        this.REQEUSTCODE = buildler.reqeustCode;
    }

    public static <T> Builder<T> Builder(@NonNull T actFmg) {
        return new Builder<>(actFmg);
    }

    public void show() {
        if (null == CANCELLISTENER) {
            throw new IllegalArgumentException("对话框取消监听按钮不能为空");
        }
        showDialog();
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(TITLE)
                .setMessage(MESSAGE)
                .setPositiveButton(POSITIVEBUTTON, this)
                .setNegativeButton(NEGATIVEBUTTON, CANCELLISTENER)
                .create()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));

        if (ACTIVITY instanceof Activity) {
            ((Activity) ACTIVITY).startActivityForResult(intent, REQEUSTCODE);
        } else if (ACTIVITY instanceof Fragment) {
            ((Fragment) ACTIVITY).startActivityForResult(intent, REQEUSTCODE);
        }
    }

    /**
     * 获取Activity对象
     *
     * @return
     */
    private Activity getActivity() {
        if (ACTIVITY instanceof Activity) {
            return (Activity) ACTIVITY;
        } else if (ACTIVITY instanceof Fragment) {
            return ((Fragment) ACTIVITY).getActivity();
        } else {
            throw illegalException;
        }
    }

    public static class Builder<T> {
        private T activity;
        private String title;//对话框标题
        private String message;//解释为什么需要这组权限的提示内容
        private String positiveButton;//确定按钮
        private String negativeButton;//取消按钮
        private DialogInterface.OnClickListener negativeListener;//对胡狂点击监听
        private int reqeustCode = -1;//跳转设置的请求标识码

        public Builder(@NonNull T actFmg) {
            if (!(actFmg instanceof Activity) && !(actFmg instanceof Fragment)) {
                throw illegalException;
            }

            this.activity = actFmg;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(String positiveButton) {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder setNegativeButton(String negativeButton) {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder setNegativeListener(DialogInterface.OnClickListener negativeListener) {
            this.negativeListener = negativeListener;
            return this;
        }

        /**
         * 跳转到设置界面的请求标识码
         *
         * @param reqeustCode
         * @return
         */
        public Builder setReqeustCode(int reqeustCode) {
            this.reqeustCode = reqeustCode;
            return this;
        }

        public AppSettingDialog build() {
            this.title = TextUtils.isEmpty(title) ? "权限授权" : title;
            this.message = TextUtils.isEmpty(message) ? "打开设置，启动权限" : message;
            this.positiveButton = TextUtils.isEmpty(positiveButton) ? getActivity().getString(android.R.string.ok) : positiveButton;
            this.negativeButton = TextUtils.isEmpty(negativeButton) ? getActivity().getString(android.R.string.cancel) : negativeButton;
            this.reqeustCode = reqeustCode > 0 ? reqeustCode : PERMISSION_SETTING_CODE;
            return new AppSettingDialog<T>(this);
        }

        /**
         * 获取Activity对象
         *
         * @return
         */
        private Activity getActivity() {
            if (activity instanceof Activity) {
                return (Activity) activity;
            } else if (activity instanceof Fragment) {
                return ((Fragment) activity).getActivity();
            } else {
                throw illegalException;
            }
        }
    }
}
