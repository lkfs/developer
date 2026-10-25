package io.github.lkfs.developer.redis.autoconfigure;

import io.github.lkfs.developer.redis.configuration.RedisConfig;
import io.github.lkfs.developer.redis.configuration.RedisLockAspect;
import io.github.lkfs.developer.redis.configuration.RedisTtlConfiguration;
import io.github.lkfs.developer.redis.configuration.RedissonConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(
        before = DataRedisAutoConfiguration.class,
        beforeName = {
                "org.redisson.spring.starter.RedissonAutoConfigurationV2",
                "org.redisson.spring.starter.RedissonAutoConfigurationV4"})
@Import({RedisConfig.class, RedisTtlConfiguration.class})
public class DeveloperRedisAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
    @Import({RedissonConfig.class, RedisLockAspect.class})
    static class RedissonConfiguration {
    }
}
