package com.anyview.service;

import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedLockService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_TIMEOUT = 30;

    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean tryLock(String lockKey, long timeout, TimeUnit timeUnit) {
        String key = LOCK_PREFIX + lockKey;
        String value = UUID.randomUUID().toString();
        
        Boolean result = redisUtil.setIfAbsent(key, value, timeout, timeUnit);
        return Boolean.TRUE.equals(result);
    }

    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        redisUtil.delete(key);
    }

    public void executeWithLock(String lockKey, Runnable task) {
        executeWithLock(lockKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS, task);
    }

    public void executeWithLock(String lockKey, long timeout, TimeUnit timeUnit, Runnable task) {
        if (tryLock(lockKey, timeout, timeUnit)) {
            try {
                task.run();
            } finally {
                unlock(lockKey);
            }
        } else {
            throw new RuntimeException("获取分布式锁失败: " + lockKey);
        }
    }
}
