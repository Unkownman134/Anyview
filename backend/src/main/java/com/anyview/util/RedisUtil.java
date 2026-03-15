package com.anyview.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ValueOperations<String, Object> valueOps() {
        return redisTemplate.opsForValue();
    }

    // 设置缓存
    public void set(String key, Object value) {
        valueOps().set(key, value);
    }

    // 设置缓存并设置过期时间
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        valueOps().set(key, value, timeout, unit);
    }

    // 仅当key不存在时设置缓存（用于分布式锁）
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return valueOps().setIfAbsent(key, value, timeout, unit);
    }

    // 获取缓存
    public Object get(String key) {
        return valueOps().get(key);
    }

    // 删除缓存
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 判断缓存是否存在
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 自增
    public Long increment(String key) {
        return valueOps().increment(key);
    }

    // 自减
    public Long decrement(String key) {
        return valueOps().decrement(key);
    }

    // 设置过期时间
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    // 获取过期时间
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
}
