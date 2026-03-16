package com.anyview.service;

import com.anyview.dto.JwtResponse;
import com.anyview.dto.LoginRequest;
import com.anyview.dto.RegisterRequest;
import com.anyview.entity.School;
import com.anyview.entity.User;
import com.anyview.repository.SchoolRepository;
import com.anyview.repository.UserRepository;
import com.anyview.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    public JwtResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 生成简单的token，实际项目中应该使用JWT
            String token = "token-" + user.getId() + "-" + new Date().getTime();

            // 缓存用户信息到Redis
            redisUtil.set("user:" + user.getId(), user);

            return new JwtResponse(
                    token,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getRealName()
            );
        } catch (Exception e) {
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRealName(request.getRealName());
        user.setRole(request.getRole().toLowerCase());
        user.setEnabled(true);

        // 非管理员用户需要选择学校
        if (!request.getRole().equalsIgnoreCase("ADMIN") && request.getSchoolId() != null) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("学校不存在"));
            user.setSchool(school);
        }

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            
            // 从数据库获取用户信息
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                // 检查Redis中是否有缓存的用户信息
                Object cachedUser = redisUtil.get("user:" + user.getId());
                if (cachedUser instanceof User) {
                    return (User) cachedUser;
                }
                // 如果Redis中没有，则缓存用户信息
                redisUtil.set("user:" + user.getId(), user);
            }
            return user;
        }
        return null;
    }
}
