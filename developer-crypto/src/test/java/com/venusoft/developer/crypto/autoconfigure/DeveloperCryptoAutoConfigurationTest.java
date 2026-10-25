package com.venusoft.developer.crypto.autoconfigure;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.venusoft.developer.common.autoconfigure.DeveloperCommonAutoConfiguration;
import com.venusoft.developer.common.autoconfigure.DeveloperJsonAutoConfiguration;
import com.venusoft.developer.crypto.CryptoRequestBodyAdvice;
import com.venusoft.developer.crypto.CryptoResponseBodyAdvice;
import com.venusoft.developer.crypto.configuration.CryptoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperCryptoAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperCommonAutoConfiguration.class,
                    DeveloperJsonAutoConfiguration.class,
                    DeveloperCryptoAutoConfiguration.class));

    private final ApplicationContextRunner nonWebRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperCommonAutoConfiguration.class,
                    DeveloperJsonAutoConfiguration.class,
                    DeveloperCryptoAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperCryptoAutoConfiguration.class.getName());
    }

    @Test
    void registersCryptoBeansWithSharedJsonConfig() {
        this.runner.run(context -> {
            assertThat(context).hasSingleBean(CryptoConfiguration.class);
            assertThat(context).hasSingleBean(CryptoRequestBodyAdvice.class);
            assertThat(context).hasSingleBean(CryptoResponseBodyAdvice.class);
            assertThat(context).hasSingleBean(FastJsonConfig.class);
            assertThat(context.getBean(FastJsonConfig.class).getDateFormat()).isEqualTo("millis");
        });
    }

    @Test
    void doesNotRegisterServletAdvicesOutsideWebApplication() {
        this.nonWebRunner.run(context -> {
            assertThat(context).doesNotHaveBean(CryptoRequestBodyAdvice.class);
            assertThat(context).doesNotHaveBean(CryptoResponseBodyAdvice.class);
        });
    }
}
