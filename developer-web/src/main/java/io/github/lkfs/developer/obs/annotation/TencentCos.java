package io.github.lkfs.developer.obs.annotation;

import java.lang.annotation.*;

/**
 * 腾讯云对象存储注解，用于自动生成对象链接（可解密
 *
 * @author Administrator
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TencentCos {
    BucketType type() default BucketType.PUBLIC; //bucket桶开放权限
}
