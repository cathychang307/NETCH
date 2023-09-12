package com.bot.cqs.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NetchLogWritingAction {
    String functionId() default "";
    boolean writeLogBeforeAction() default false;
    boolean writeSuccessLogAfterAction() default false;
}
