package com.hyj.lib.permission.callback;

import java.util.List;

/**
 * <pre>
 *     权限回调接口
 * </pre>
 * Author：hyj
 * Date：2019/1/3 22:35
 */
public interface IPermissionCallback {
    /**
     * 授予授权通过的回调方法
     *
     * @param reqeustCode 权限请求码
     * @param perms       请求权限组(方便debug,如用户只授权权限组部分权限)
     */
    void onPermissionGranted(int reqeustCode, List<String> perms);

    /**
     * 权限拒绝回调
     *
     * @param reqeustCode 权限请求标识码
     * @param perms       请求的权限组
     */
    void onPermissionDenied(int reqeustCode, List<String> perms);

    /**
     * 权限拒绝，且勾选了不再询问回调
     *
     * @param reqeustCode
     * @param perms
     */
    void onPermissionPermanetlyDenied(int reqeustCode, List<String> perms);
}