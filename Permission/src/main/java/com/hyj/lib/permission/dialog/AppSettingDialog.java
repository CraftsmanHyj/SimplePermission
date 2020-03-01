package com.hyj.lib.permission.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hyj.lib.permission.utils.PermUtils;

/**
 * <pre>
 *     弹出设置提示框
 * </pre>
 * Author：hyj
 * Date：2019/1/4 22:05
 */
public class AppSettingDialog {
    private final Context CONTEXT;//activity或fragment对象
    private final String TITLE;//对话框标题
    private final CharSequence MESSAGE;//解释为什么需要这组权限的提示内容
    private final String POSITIVEBUTTON;//确定按钮
    private final DialogInterface.OnClickListener POSITIVELISTENER;//对话框确定按钮监听
    private final String NEGATIVEBUTTON;//取消按钮
    private final DialogInterface.OnClickListener NEGATIVELISTENER;//对话框取消按钮监听

    private AppSettingDialog(Builder buildler) {
        this.CONTEXT = buildler.mContext;
        this.TITLE = buildler.title;
        this.MESSAGE = buildler.message;
        this.POSITIVEBUTTON = buildler.positiveButton;
        this.POSITIVELISTENER = buildler.positiveListener;
        this.NEGATIVEBUTTON = buildler.negativeButton;
        this.NEGATIVELISTENER = buildler.negativeListener;
    }

    public static Builder Builder(@NonNull Context cxt) {
        return new Builder(cxt);
    }

    public void show() {
        if (null == NEGATIVELISTENER) {
            throw new IllegalArgumentException("对话框取消监听按钮不能为空");
        }
        showDialog();
    }

    private void showDialog() {
        new AlertDialog.Builder(PermUtils.getActivity(CONTEXT))
                .setCancelable(false)
                .setTitle(TITLE)
                .setMessage(MESSAGE)
                .setPositiveButton(POSITIVEBUTTON, POSITIVELISTENER)
                .setNegativeButton(NEGATIVEBUTTON, NEGATIVELISTENER)
                .create()
                .show();
    }

    public static class Builder {
        private Context mContext;
        private String title;//对话框标题
        private CharSequence message;//解释为什么需要这组权限的提示内容
        private String positiveButton;//确定按钮
        private DialogInterface.OnClickListener positiveListener;//取消按钮点击监听
        private String negativeButton;//取消按钮
        private DialogInterface.OnClickListener negativeListener;//取消按钮点击监听

        public Builder(Context cxt) {
            this.mContext = cxt;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(String positiveButton) {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder setPositiveListener(DialogInterface.OnClickListener positiveListener) {
            this.positiveListener = positiveListener;
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

        public AppSettingDialog build() {
            Activity mAct = PermUtils.getActivity(mContext);
            this.title = TextUtils.isEmpty(title) ? "权限授权" : title;
            this.message = TextUtils.isEmpty(message) ? "打开设置，启动权限" : message;
            this.positiveButton = TextUtils.isEmpty(positiveButton) ? mAct.getString(android.R.string.ok) : positiveButton;
            this.negativeButton = TextUtils.isEmpty(negativeButton) ? mAct.getString(android.R.string.cancel) : negativeButton;
            return new AppSettingDialog(this);
        }
    }
}