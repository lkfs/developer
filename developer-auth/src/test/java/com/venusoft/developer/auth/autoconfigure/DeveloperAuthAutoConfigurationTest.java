package com.venusoft.developer.auth.autoconfigure;

import com.venusoft.developer.auth.aspect.PreAuthorizeAspect;
import com.venusoft.developer.auth.security.SpringSecurityAuditorAware;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperAuthAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DeveloperAuthAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperAuthAutoConfiguration.class.getName());
    }

    @Test
    void registersAuthBeans() {
        this.runner.run(context -> {
            assertThat(context).hasSingleBean(PreAuthorizeAspect.class);
            assertThat(context).hasSingleBean(SpringSecurityAuditorAware.class);
        });
    }
}
