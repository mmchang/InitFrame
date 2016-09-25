package com.lnpc.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义应用系统的前台拦截
 * @author cjq
 *
 */
@Documented
@Inherited
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface WebPassport {

}
