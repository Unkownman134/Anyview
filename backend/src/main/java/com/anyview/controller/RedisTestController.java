package com.anyview.controller;

import com.anyview.annotation.Cacheable;
import com.anyview.annotation.RateLimiter;
import com.anyview.service.DistributedLockService;
import com.anyview.service.SessionService;
import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistributedLockService distributedLockService;

    @PostMapping("/set")
    public Map<String, Object> setCache(@RequestParam String key, @RequestParam String value) {
        redisUtil.set(key, value);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存设置成功");
        return result;
    }

    @GetMapping("/get")
    public Map<String, Object> getCache(@RequestParam String key) {
        Object value = redisUtil.get(key);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("value", value);
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteCache(@RequestParam String key) {
        redisUtil.delete(key);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存删除成功");
        return result;
    }

    @PostMapping("/session")
    public Map<String, Object> saveSession(@RequestParam String userId, @RequestParam String sessionData) {
        sessionService.saveSession(userId, sessionData);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "会话保存成功");
        return result;
    }

    @GetMapping("/session")
    public Map<String, Object> getSession(@RequestParam String userId) {
        Object sessionData = sessionService.getSession(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sessionData", sessionData);
        return result;
    }

    @PostMapping("/lock")
    public Map<String, Object> testLock(@RequestParam String lockKey) {
        boolean locked = distributedLockService.tryLock(lockKey);
        Map<String, Object> result = new HashMap<>();
        result.put("success", locked);
        result.put("message", locked ? "获取锁成功" : "获取锁失败");
        return result;
    }

    @DeleteMapping("/lock")
    public Map<String, Object> unlock(@RequestParam String lockKey) {
        distributedLockService.unlock(lockKey);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "释放锁成功");
        return result;
    }

    @GetMapping("/rate-limit")
    @RateLimiter(key = "test_rate_limit", limit = 5, timeout = 1, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public Map<String, Object> testRateLimit() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "请求成功，未触发限流");
        return result;
    }

    @GetMapping("/test-cacheable")
    @Cacheable(key = "test_cacheable", timeout = 5)
    public Map<String, Object> testCacheable() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存测试成功");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
