package com.hyj.permission.demo;

import android.Manifest;

/**
 * <pre>
 *     动态申请权限枚举类
 * </pre>
 * Author：hyj
 * Date：2019/2/10 20:50
 */
public enum PermsEnm {
    CAMER(1000, Manifest.permission.CAMERA),
    LOCATION_CONTACTS(1001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS);

    private int code;
    private String[] perms;

    PermsEnm(int code, String... perms) {
        this.code = code;
        this.perms = perms;
    }

    public int getCode() {
        return code;
    }

    public String[] getPerms() {
        return perms;
    }
}