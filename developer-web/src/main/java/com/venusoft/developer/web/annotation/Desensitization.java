package com.venusoft.developer.web.annotation;


import java.lang.annotation.*;

/**
 * 脱敏注解，用于返回的DTO对象中的手机号、身份证等字段
 * @author : Luis
 * @date : 2021/4/15 17:26
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Desensitization {

    DesensitizationType type();

}
