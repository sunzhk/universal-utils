package com.sunzhk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在混淆时过滤掉带有该注解的整个类。
 * 需要在混淆文件proguard-rules.pro中加入
 * -keep,allowobfuscation @interface com.sunzhk.annotation.KeepAll
 * -keep @com.sunzhk.annotation.KeepAll class * {*;}
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface KeepAll {
}
