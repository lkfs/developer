package com.venusoft.developer.redis.configuration;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ConstantDelay;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author pc
 * @ClassName : RedissionConfig
 * @Description : redission
 * @Date: 2021/06/09
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database}")
    private Integer database;

    @Bean
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        config.setPassword(password);
        config.useSingleServer().setDatabase(database);
        config.useSingleServer().setIdleConnectionTimeout(10000);
        config.useSingleServer().setConnectTimeout(10000);
        config.useSingleServer().setTimeout(3000);
        config.useSingleServer().setRetryAttempts(3);
        config.useSingleServer().setRetryDelay(new ConstantDelay(Duration.ofMillis(1500)));
        config.useSingleServer().setSubscriptionsPerConnection(5);
        config.useSingleServer().setClientName("myredisson");
        config.useSingleServer().setSubscriptionConnectionMinimumIdleSize(1);
        config.useSingleServer().setSubscriptionConnectionPoolSize(50);
        config.useSingleServer().setConnectionMinimumIdleSize(4);
        config.useSingleServer().setConnectionPoolSize(64);
        config.useSingleServer().setDnsMonitoringInterval(5000);
        config.setCodec(new JsonJacksonCodec());
        config.setTransportMode(TransportMode.NIO);

        return Redisson.create(config);
    }
}
