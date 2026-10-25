package io.github.lkfs.developer.common.autoconfigure;

import com.alibaba.fastjson2.filter.ValueFilter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.converter.HttpMessageConverters;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperJsonAutoConfigurationTest {

    private final WebApplicationContextRunner webRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperJsonAutoConfiguration.class,
                    HttpMessageConvertersAutoConfiguration.class));

    private final ApplicationContextRunner nonWebRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DeveloperJsonAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperJsonAutoConfiguration.class.getName());
    }

    @Test
    void registersSingleConfigWithMillisDateFormat() {
        this.webRunner.run(context -> {
            assertThat(context).hasSingleBean(FastJsonConfig.class);
            assertThat(context).hasBean("fastJsonHttpMessageConverters");
            assertThat(context.getBean(FastJsonConfig.class).getDateFormat()).isEqualTo("millis");
        });
    }

    @Test
    void customizerRegistersFastJsonConverter() {
        this.webRunner.run(context -> {
            HttpMessageConverters.ServerBuilder builder = HttpMessageConverters.forServer();
            context.getBean("fastJsonHttpMessageConverters", ServerHttpMessageConvertersCustomizer.class)
                    .customize(builder);
            assertThat(builder.build())
                    .anyMatch(FastJsonHttpMessageConverter.class::isInstance);
        });
    }

    @Test
    void collectsFilterBeans() {
        this.webRunner
                .withBean(ValueFilter.class, () -> (ValueFilter) (object, name, value) -> value)
                .run(context -> assertThat(context.getBean(FastJsonConfig.class).getWriterFilters())
                        .hasSize(1));
    }

    @Test
    void doesNotRegisterConfigOutsideWebApplication() {
        this.nonWebRunner.run(context -> {
            assertThat(context).doesNotHaveBean(FastJsonConfig.class);
            assertThat(context).doesNotHaveBean("fastJsonHttpMessageConverters");
        });
    }
}
