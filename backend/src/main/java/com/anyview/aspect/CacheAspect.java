package com.anyview.aspect;

import com.anyview.annotation.Cacheable;
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
public class CacheAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Around("@annotation(com.anyview.annotation.Cacheable)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        String key = cacheable.key();
        long timeout = cacheable.timeout();
        TimeUnit timeUnit = cacheable.timeUnit();

        Object cachedValue = redisUtil.get(key);
        if (cachedValue != null) {
            return cachedValue;
        }

        Object result = joinPoint.proceed();
        redisUtil.set(key, result, timeout, timeUnit);

        return result;
    }
}
