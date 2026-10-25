package com.venusoft.developer.web.autoconfigure;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.aliyun.oss.OSS;
import com.amazonaws.services.s3.AmazonS3;
import com.obs.services.ObsClient;
import com.qcloud.cos.COSClient;
import com.qiniu.util.Auth;
import com.venusoft.developer.common.autoconfigure.DeveloperJsonAutoConfiguration;
import com.venusoft.developer.obs.service.AliOssService;
import com.venusoft.developer.obs.service.IBaseStorageService;
import com.venusoft.developer.obs.service.QiniuKodoService;
import com.venusoft.developer.web.controller.GlobalExceptionHandler;
import com.venusoft.developer.web.controller.UploadController;
import com.venusoft.developer.web.converter.filter.AliOssFilter;
import com.venusoft.developer.web.converter.filter.DesensitizationFilter;
import com.venusoft.developer.web.converter.filter.EnumFilter2;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperWebAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperJsonAutoConfiguration.class,
                    DeveloperWebAutoConfiguration.class,
                    HttpMessageConvertersAutoConfiguration.class,
                    ValidationAutoConfiguration.class))
            .withPropertyValues("qiniu.kodo.accessKey=test", "qiniu.kodo.secretKey=test");

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperWebAutoConfiguration.class.getName());
    }

    @Test
    void registersCoreBeansWhenObsSdksAreMissing() {
        this.runner.withClassLoader(new FilteredClassLoader(
                        OSS.class, ObsClient.class, COSClient.class, Auth.class, AmazonS3.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(UploadController.class);
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                    assertThat(context).hasBean("fastJsonHttpMessageConverters");
                    assertThat(context).hasSingleBean(Validator.class);
                    assertThat(context).hasSingleBean(DesensitizationFilter.class);
                    assertThat(context).hasSingleBean(EnumFilter2.class);
                    assertThat(context).doesNotHaveBean(IBaseStorageService.class);
                });
    }

    @Test
    void registersNoStorageServiceWhenSdksPresentButNotConfigured() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        DeveloperJsonAutoConfiguration.class,
                        DeveloperWebAutoConfiguration.class,
                        HttpMessageConvertersAutoConfiguration.class,
                        ValidationAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(IBaseStorageService.class);
                });
    }

    @Test
    void registersAliServiceWhenCredentialsAreConfigured() {
        this.runner.withPropertyValues(
                        "ali.oss.accessKeyId=test",
                        "ali.oss.accessKeySecret=test",
                        "ali.oss.endpoint=oss-cn-beijing.aliyuncs.com")
                .withClassLoader(new FilteredClassLoader(
                        ObsClient.class, COSClient.class, AmazonS3.class, Auth.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(AliOssService.class);
                    assertThat(context).hasSingleBean(AliOssFilter.class);
                });
    }

    @Test
    void registersQiniuServiceWhenSdkIsPresent() {
        this.runner.withClassLoader(new FilteredClassLoader(
                        OSS.class, ObsClient.class, COSClient.class, AmazonS3.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(QiniuKodoService.class);
                    assertThat(context).doesNotHaveBean(AliOssService.class);
                    assertThat(context.getBean(FastJsonConfig.class).getWriterFilters())
                            .extracting(filter -> filter.getClass().getSimpleName())
                            .containsExactly("DesensitizationFilter", "EnumFilter2", "QiniuKodoFilter");
                });
    }
}
