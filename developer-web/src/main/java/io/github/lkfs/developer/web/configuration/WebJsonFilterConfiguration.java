package io.github.lkfs.developer.web.configuration;

import io.github.lkfs.developer.web.converter.filter.DesensitizationFilter;
import io.github.lkfs.developer.web.converter.filter.EnumFilter2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * web 模块贡献给公共 fastjson 配置的过滤器。
 */
@Configuration(proxyBeanMethods = false)
public class WebJsonFilterConfiguration {

    @Bean
    @Order(1)
    public DesensitizationFilter desensitizationFilter() {
        return new DesensitizationFilter();
    }

    @Bean
    @Order(2)
    public EnumFilter2 enumFilter2() {
        return new EnumFilter2();
    }
}
