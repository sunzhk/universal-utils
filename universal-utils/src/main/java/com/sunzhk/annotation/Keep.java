package com.sunzhk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在混淆时过滤掉带有该注解的类(不包括类里面的方法、参数)、方法、参数等。
 * 需要在混淆文件proguard-rules.pro中加入
 * -keep,allowobfuscation @interface com.sunzhk.annotation.Keep
 * -keep @com.sunzhk.annotation.Keep class *
 * -keepclassmembers class * {
 *     @com.sunzhk.annotation.Keep *;
 * }
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Keep {
}
