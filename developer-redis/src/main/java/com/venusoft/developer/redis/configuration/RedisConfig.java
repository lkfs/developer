package com.venusoft.developer.redis.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import io.lettuce.core.api.StatefulConnection;
import jakarta.annotation.Resource;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 参照：https://rstyro.github.io/blog/2019/04/16/SpringBoot%E4%B8%8ERedisCacheManager%E6%95%B4%E5%90%88/
 *
 * @author : Luis
 * @date : 2021/4/14 9:29
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host:#{'127.0.0.1'}}")
    private String hostName;

    @Value("${spring.data.redis.port:#{6379}}")
    private int port;

    @Value("${spring.data.redis.password:#{123456}}")
    private String password;

    @Value("${spring.data.redis.database:#{0}}")
    private int databaseId;

    @Value("${spring.data.redis.timeout:#{3000}}")
    private int timeout;

    @Value("${spring.data.redis.lettuce.pool.max-active:#{8}}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle:#{8}}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:#{0}}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:#{1000}}")
    private long maxWaitMillis;

    /**
     * 允许反序列化自动类型的类名前缀，范围越小越安全
     */
    @Value("${developer.redis.auto-type-packages:com.venusoft.,java.util.,java.math.}")
    private String[] autoTypePackages;

    @Resource
    private RedisTtlConfiguration ttlConfiguration;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(hostName, port);

        // 设置选用的数据库号码
        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(databaseId);

        // 设置 redis 数据库密码
        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(password);

        // 连接池配置
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWait(Duration.ofMillis(maxWaitMillis));

        LettucePoolingClientConfiguration lettucePoolingClientConfiguration
                = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(poolConfig)
                .build();

        // 根据配置和客户端配置创建连接，生命周期由 Spring 管理
        return new LettuceConnectionFactory(redisConfiguration, lettucePoolingClientConfiguration);
    }

    /**
     * 缓存管理器
     *
     * @return
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory connectionFactory) {

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        //设置缓存中的对象反序列化时仍为对象，否则为LinkedHashMap
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        BasicPolymorphicTypeValidator.Builder validatorBuilder = BasicPolymorphicTypeValidator.builder();
        for (String autoTypePackage : autoTypePackages) {
            validatorBuilder.allowIfSubType(autoTypePackage);
        }
        validatorBuilder.allowIfSubTypeIsArray();
        om.activateDefaultTyping(validatorBuilder.build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //默认的缓存过期时间10分钟
                .entryTtl(Duration.ofMinutes(10))
                // 配置序列化（解决乱码的问题）
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        //自定义的缓存过期时间
        Map<String, RedisCacheConfiguration> ttlMap = ttlConfiguration.getTtl()
                .keySet()
                .stream()
                .collect(Collectors.toMap(
                        key -> key,
                        value -> {
                            Integer ttl = ttlConfiguration.getTtl().get(value);
                            return config.entryTtl(Duration.ofSeconds(ttl));
                        }
                ));
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(ttlMap)
                .build();

        return cacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        //使用Fastjson2JsonRedisSerializer来序列化和反序列化redis的value值 by zhengkai
        FastJson2RedisSerializer fastJson2JsonRedisSerializer = new FastJson2RedisSerializer(Object.class, autoTypePackages);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
