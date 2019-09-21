package com.hyj.permission.demo;

import android.Manifest;

import com.hyj.lib.permission.bean.IPermissionInfo;
import com.hyj.lib.permission.bean.PermConstant;

/**
 * <pre>
 *     动态申请权限枚举类
 * </pre>
 * Author：hyj
 * Date：2019/2/10 20:50
 */
public enum PermsEnm implements IPermissionInfo {
    CAMER(Manifest.permission.CAMERA),
    LOCATION_CONTACTS(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS);

    private int code;
    private String[] perms;

    PermsEnm(String... perms) {
        this.code = PermConstant.getReqeustCode();
        this.perms = perms;
    }

    @Override
    public int getRequestCode() {
        return code;
    }

    @Override
    public String[] getPermissions() {
        return perms;
    }
}