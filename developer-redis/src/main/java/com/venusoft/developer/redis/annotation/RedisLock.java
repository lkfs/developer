package com.venusoft.developer.redis.annotation;

import java.lang.annotation.*;

/**
 * @author : Luis
 * @date : 2021/12/13 13:58
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface RedisLock {
    String key();

    // 并发锁key
    String value() default "";

    // 等锁等待时长，超时则放弃加锁，单位毫秒
    long ttl() default 3000;

    /**
     * 锁的持有时长，单位毫秒。
     * 默认 -1：启用 Redisson watchdog 自动续期（每 lockWatchdogTimeout/3 续一次），
     * 业务执行完毕后手动 unlock 即可，不会因业务执行时间过长而导致锁自动过期。
     * 设为正数则使用固定 leaseTime，到期自动释放（不推荐，易出现上述 IllegalMonitorStateException）。
     */
    long leaseTime() default -1;
}
