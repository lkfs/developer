package com.venusoft.developer.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加与WebMvc相关的自定义配置
 *
 * @author 707669522@qq.com
 * @since 2020-06-11
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // MDC
        LogMdcInterceptor logMdcInterceptor = new LogMdcInterceptor();
        registry.addInterceptor(logMdcInterceptor);
        //可以具体制定哪些需要拦截，哪些不拦截，其实也可以使用自定义注解更灵活完成
        //.addPathPatterns("/**")
        //.excludePathPatterns("/testxx.html");
    }
}