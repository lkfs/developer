package io.github.lkfs.developer.redis.autoconfigure;

import io.github.lkfs.developer.redis.configuration.FastJson2RedisSerializer;
import io.github.lkfs.developer.redis.configuration.RedisLockAspect;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class DeveloperRedisAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DeveloperRedisAutoConfiguration.class,
                    DataRedisAutoConfiguration.class));

    @Test
    void autoConfigurationIsListedInImportsFile() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader()).getCandidates())
                .contains(DeveloperRedisAutoConfiguration.class.getName());
    }

    @Test
    void registersCoreRedisBeansBeforeBootAutoConfiguration() {
        this.runner.withClassLoader(new FilteredClassLoader(RedissonClient.class))
                .run(context -> {
                    assertThat(context).hasBean("redisTemplate");
                    RedisTemplate<?, ?> redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);
                    assertThat(redisTemplate.getValueSerializer()).isInstanceOf(FastJson2RedisSerializer.class);
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context).doesNotHaveBean(RedisLockAspect.class);
                });
    }
}
