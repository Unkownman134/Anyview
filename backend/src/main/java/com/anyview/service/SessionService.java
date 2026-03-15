package com.anyview.service;

import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final long SESSION_TIMEOUT = 30;

    public void saveSession(String userId, Object sessionData) {
        String key = SESSION_PREFIX + userId;
        redisUtil.set(key, sessionData, SESSION_TIMEOUT, TimeUnit.MINUTES);
        
        // 记录用户的会话列表
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        redisUtil.set(userSessionsKey, System.currentTimeMillis(), SESSION_TIMEOUT, TimeUnit.MINUTES);
    }

    public Object getSession(String userId) {
        String key = SESSION_PREFIX + userId;
        return redisUtil.get(key);
    }

    public void removeSession(String userId) {
        String key = SESSION_PREFIX + userId;
        redisUtil.delete(key);
        
        // 删除用户的会话记录
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        redisUtil.delete(userSessionsKey);
    }

    public void updateSession(String userId, Object sessionData) {
        saveSession(userId, sessionData);
    }

    public boolean existsSession(String userId) {
        String key = SESSION_PREFIX + userId;
        return redisUtil.exists(key);
    }
    
    public void createLoginSession(String userId, String username, String ip, String userAgent) {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("userId", userId);
        sessionData.put("username", username);
        sessionData.put("loginTime", System.currentTimeMillis());
        sessionData.put("ip", ip);
        sessionData.put("userAgent", userAgent);
        sessionData.put("lastActivity", System.currentTimeMillis());
        
        saveSession(userId, sessionData);
    }
    
    public void updateLastActivity(String userId) {
        Object sessionData = getSession(userId);
        if (sessionData instanceof Map) {
            Map<String, Object> session = (Map<String, Object>) sessionData;
            session.put("lastActivity", System.currentTimeMillis());
            updateSession(userId, session);
        }
    }
    
    public long getSessionAge(String userId) {
        Object sessionData = getSession(userId);
        if (sessionData instanceof Map) {
            Map<String, Object> session = (Map<String, Object>) sessionData;
            Long loginTime = (Long) session.get("loginTime");
            if (loginTime != null) {
                return System.currentTimeMillis() - loginTime;
            }
        }
        return -1;
    }
}
