package io.github.lkfs.developer.redis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * spring Cacheable的过期时间
 *
 * @author : lkfs
 * @date : 2023/1/25 21:13
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.cache")
public class RedisTtlConfiguration {
    private Map<String, Integer> ttl = new HashMap<>();
}
