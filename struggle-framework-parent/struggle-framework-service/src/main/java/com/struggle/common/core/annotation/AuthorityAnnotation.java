package com.struggle.common.core.annotation;

import java.lang.annotation.*;

/**
 * Class Name : AuthorityAnnotation<br>
 * 
 * Description : 权限注解<br>
 *
 * @version $Revision$
 * @see
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorityAnnotation {
    // 是否需要机构数据权限
    boolean permission() default false;
    // 需要直接报错的权限状态：数据权限类型 1：无数据权限 2：全部 3：机构范围权限，并且有机构 4、机构范围权限，并且没有机构
    String permissionErr() default "1,4";
    // 获取当前传入的code
    String value() default "";
}
