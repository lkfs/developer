package com.venusoft.developer.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.venusoft.developer.mybatisplus.configuration.MyMetaObjectHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperMybatisPlusAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DeveloperMybatisPlusAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperMybatisPlusAutoConfiguration.class.getName());
    }

    @Test
    void registersMybatisPlusBeans() {
        this.runner.run(context -> {
            assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
            assertThat(context).hasSingleBean(MyMetaObjectHandler.class);
        });
    }
}
