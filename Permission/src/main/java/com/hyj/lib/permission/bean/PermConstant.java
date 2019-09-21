package com.hyj.lib.permission.bean;

/**
 * <pre>
 *     常量类
 * </pre>
 * Author：hyj
 * Date：2019/9/21 13:23
 */
public final class PermConstant {
    private static int requestCode = 20190921;

    /**
     * 获取一个权限请求码
     *
     * @return
     */
    public synchronized static int getReqeustCode() {
        return requestCode++;
    }
}