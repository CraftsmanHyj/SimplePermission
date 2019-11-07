package com.hyj.permission.demo;

import android.content.Context;
import android.widget.Toast;

/**
 * <pre>
 *     Toast工具类
 * </pre>
 * Author：hyj
 * Date：2019/9/21 12:42
 */
class ToastUtils {
    /**
     * 弹出LENGTH_SHORT的Toast
     *
     * @param cxt
     * @param msg
     */
    public static void showToast(Context cxt, String msg) {
        Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show();
    }
}