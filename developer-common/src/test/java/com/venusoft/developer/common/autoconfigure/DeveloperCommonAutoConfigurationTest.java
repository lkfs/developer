package com.venusoft.developer.common.autoconfigure;

import com.venusoft.developer.common.security.AesUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperCommonAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DeveloperCommonAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperCommonAutoConfiguration.class.getName());
    }

    @Test
    void registersCommonBeans() {
        this.runner.run(context -> {
            assertThat(context).hasSingleBean(AesUtils.class);
        });
    }
}
