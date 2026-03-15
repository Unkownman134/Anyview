package com.anyview.aspect;

import com.anyview.annotation.RateLimiter;
import com.anyview.util.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RedisUtil redisUtil;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Around("@annotation(com.anyview.annotation.RateLimiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

        String key = RATE_LIMIT_PREFIX + rateLimiter.key();
        int limit = rateLimiter.limit();
        long timeout = rateLimiter.timeout();
        TimeUnit timeUnit = rateLimiter.timeUnit();

        Long currentCount = (Long) redisUtil.get(key);
        if (currentCount == null) {
            currentCount = 0L;
        }

        if (currentCount >= limit) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        redisUtil.increment(key);
        if (currentCount == 0) {
            redisUtil.expire(key, timeout, timeUnit);
        }

        return joinPoint.proceed();
    }
}
