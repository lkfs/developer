package com.venusoft.developer.web.autoconfigure;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.aliyun.oss.OSS;
import com.amazonaws.services.s3.AmazonS3;
import com.obs.services.ObsClient;
import com.qcloud.cos.COSClient;
import com.qiniu.util.Auth;
import com.venusoft.developer.common.autoconfigure.DeveloperCommonAutoConfiguration;
import com.venusoft.developer.common.autoconfigure.DeveloperJsonAutoConfiguration;
import com.venusoft.developer.crypto.autoconfigure.DeveloperCryptoAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CombinedJsonAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperCommonAutoConfiguration.class,
                    DeveloperJsonAutoConfiguration.class,
                    DeveloperWebAutoConfiguration.class,
                    DeveloperCryptoAutoConfiguration.class))
            .withClassLoader(new FilteredClassLoader(
                    OSS.class, ObsClient.class, COSClient.class, Auth.class, AmazonS3.class));

    @Test
    void webAndCryptoShareSingleJsonConfiguration() {
        this.runner.run(context -> {
            assertThat(context).hasSingleBean(FastJsonConfig.class);
            assertThat(context).hasBean("fastJsonHttpMessageConverters");
            assertThat(context.getBean(FastJsonConfig.class).getDateFormat()).isEqualTo("millis");
        });
    }
}
