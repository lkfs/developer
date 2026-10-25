package com.venusoft.developer.obs.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface QiniuKodo {
    BucketType type() default BucketType.PUBLIC; //bucket桶开放权限
}
