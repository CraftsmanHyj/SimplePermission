package com.hyj.lib.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * </pre>
 * Author：hyj
 * Date：2019/1/4 23:29
 */
@Target(ElementType.METHOD) //作用在方法上
@Retention(RetentionPolicy.RUNTIME) //JVM运行时通过反射获取注解的值
public @interface IPermission {
    int value();
}