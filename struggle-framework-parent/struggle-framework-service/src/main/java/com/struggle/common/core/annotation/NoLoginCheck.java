package com.struggle.common.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLoginCheck {
    // 是否需要解析用户信息
    boolean loadUser() default false;

}
