package com.aiitec.hiim.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2017/3/17.
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {
    int value();
}
