package com.hyj.lib.permission.bean;

/**
 * <pre>
 *     权限申请需要的信息
 * </pre>
 * Author：hyj
 * Date：2019/9/21 13:19
 */
public interface IPermissionInfo {
    /**
     * 权限请求码
     *
     * @return
     */
    int getRequestCode();

    /**
     * 要请求的权限数组
     *
     * @return
     */
    String[] getPermissions();
}