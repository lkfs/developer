package com.venusoft.developer.obs.annotation;


import java.lang.annotation.*;

/**
 * 阿里云对象存储注解，用于自动生成对象链接（可解密
 *
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AliOss {

    BucketType type() default BucketType.PUBLIC; //bucket桶开放权限

}
