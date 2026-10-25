package io.github.lkfs.developer.redis.configuration;

import io.github.lkfs.developer.common.exception.BizException;
import io.github.lkfs.developer.redis.annotation.RedisLock;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 参考：https://www.cnblogs.com/wsxdev/p/12015100.html
 * CacheKeyGenerator
 *
 * @author : Luis
 * @date : 2021/12/13 14:00
 */
@Aspect
@Slf4j
@Order(1)
@Component
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Around(value = "@annotation(redisLock)")
    public Object redisLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String lockKey = this.getLockKey(joinPoint, redisLock);
        Object output;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean ifLock = lock.tryLock(redisLock.ttl(), redisLock.leaseTime(), TimeUnit.MILLISECONDS);
            if (ifLock) {
                if (redisLock.leaseTime() == -1) {
                    log.info("加锁成功（watchdog自动续期）[{}]", lockKey);
                } else {
                    log.info("加锁成功（leaseTime={}ms）[{}]", redisLock.leaseTime(), lockKey);
                }
                output = joinPoint.proceed();
            } else {
                throw BizException.BUSINESS_FAULT.withMessage("加锁等待超时，" + lockKey);
            }
        } catch (Throwable e) {
            log.error("加锁过程中, 业务异常:{}", e.getMessage());
            if (e instanceof BizException) {
                BizException b = (BizException) e;
                log.error("aop redis distributed lock error:{}", b.getMessage());
                throw b;
            } else {
                log.error("aop redis distributed lock error:{}", e.getLocalizedMessage());
                throw BizException.BUSINESS_FAULT.withMessage(e.getLocalizedMessage());
            }
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("解锁成功[{}]", lockKey);
                } else {
                    log.info("锁已过期或被其他线程持有，无需解锁[{}]", lockKey);
                }
            } catch (Exception e) {
                log.warn("解锁异常（锁可能已因TTL超时自动释放）[{}]: {}", lockKey, e.getMessage());
            }
        }
        return output;

    }

    /**
     * 计算锁的key值，支持SpEL表达式
     *
     * @param proceedingJoinPoint
     * @param redisLock
     * @return
     */
    private String getLockKey(ProceedingJoinPoint proceedingJoinPoint, RedisLock redisLock) {
        String key = redisLock.key();
        String value = redisLock.value();

        if (ObjectUtils.isEmpty(value)) {
            return key;
        } else {
            try {
                Object[] arg = proceedingJoinPoint.getArgs();
                Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
                // 获取被拦截方法参数名列表(使用Spring支持类库)
                StandardReflectionParameterNameDiscoverer localVariableTable = new StandardReflectionParameterNameDiscoverer();
                String[] parameterNames = localVariableTable.getParameterNames(method);
                // 使用SPEL进行key的解析
                ExpressionParser parser = new SpelExpressionParser();
                // SPEL上下文
                StandardEvaluationContext context = new StandardEvaluationContext();
                // 把方法参数放入SPEL上下文中
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], arg[i]);
                }
                // 使用变量方式传入业务动态数据
                if (value.matches("^#.*.$")) {
                    value = parser.parseExpression(value).getValue(context, String.class);
                }
            } catch (EvaluationException e) {
                log.error("计算redis key 异常", e);
                throw BizException.BUSINESS_FAULT.withMessage(e.getLocalizedMessage());
            } catch (ParseException e) {
                log.error("转换redis key 异常", e);
                throw BizException.BUSINESS_FAULT.withMessage(e.getLocalizedMessage());
            }
            return key + value;
        }
    }
}
