package com.venusoft.developer.web.autoconfigure;

import com.venusoft.developer.obs.service.AliOssService;
import com.venusoft.developer.obs.service.AmazonS3Service;
import com.venusoft.developer.obs.service.HuaweiObsService;
import com.venusoft.developer.obs.service.QiniuKodoService;
import com.venusoft.developer.obs.service.TencentCosService;
import com.venusoft.developer.web.configuration.ValidatorConfiguration;
import com.venusoft.developer.web.configuration.WebJsonFilterConfiguration;
import com.venusoft.developer.web.configuration.WebMvcConfig;
import com.venusoft.developer.web.controller.GlobalExceptionHandler;
import com.venusoft.developer.web.controller.UploadController;
import com.venusoft.developer.web.converter.filter.AliOssFilter;
import com.venusoft.developer.web.converter.filter.HuaweiObsFilter;
import com.venusoft.developer.web.converter.filter.QiniuKodoFilter;
import com.venusoft.developer.web.converter.filter.TencentCosFilter;
import com.venusoft.developer.web.secret.TimespanRequestBodyAdvice;
import com.venusoft.developer.web.secret.TimespanResponseBodyAdvice;
import com.venusoft.developer.web.utils.IdUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@AutoConfiguration(before = ValidationAutoConfiguration.class)
@Import({
        WebMvcConfig.class,
        ValidatorConfiguration.class,
        WebJsonFilterConfiguration.class,
        GlobalExceptionHandler.class,
        UploadController.class,
        IdUtils.class,
        TimespanRequestBodyAdvice.class,
        TimespanResponseBodyAdvice.class
})
public class DeveloperWebAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.aliyun.oss.OSS")
    @ConditionalOnProperty(prefix = "ali.oss", name = "accessKeyId")
    @Import(AliOssService.class)
    static class AliOssConfiguration {
        @Bean
        @Order(3)
        AliOssFilter aliOssFilter(AliOssService aliOssService) {
            return new AliOssFilter(aliOssService);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.obs.services.ObsClient")
    @ConditionalOnProperty(prefix = "huawei.obs", name = "accessKey")
    @Import(HuaweiObsService.class)
    static class HuaweiObsConfiguration {
        @Bean
        @Order(4)
        HuaweiObsFilter huaweiObsFilter(HuaweiObsService huaweiObsService) {
            return new HuaweiObsFilter(huaweiObsService);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.qcloud.cos.COSClient")
    @ConditionalOnProperty(prefix = "tencent.cos", name = "secretId")
    @Import(TencentCosService.class)
    static class TencentCosConfiguration {
        @Bean
        @Order(5)
        TencentCosFilter tencentCosFilter(TencentCosService tencentCosService) {
            return new TencentCosFilter(tencentCosService);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.qiniu.util.Auth")
    @ConditionalOnProperty(prefix = "qiniu.kodo", name = "accessKey")
    @Import(QiniuKodoService.class)
    static class QiniuKodoConfiguration {
        @Bean
        @Order(6)
        QiniuKodoFilter qiniuKodoFilter(QiniuKodoService qiniuKodoService) {
            return new QiniuKodoFilter(qiniuKodoService);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.amazonaws.services.s3.AmazonS3")
    @ConditionalOnProperty(prefix = "aws.s3", name = "accessKey")
    @Import(AmazonS3Service.class)
    static class AmazonS3Configuration {
    }
}
