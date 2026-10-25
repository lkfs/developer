package com.venusoft.developer.cloud.feign.configuration;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignSupportConfig {
    public FeignSupportConfig() {
        log.info("FeignSupportConfig inited.");
    }

    /**
     * feign请求拦截器
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignInterceptor();
    }

}