package com.anyview.service;

import com.anyview.config.JwtTokenProvider;
import com.anyview.dto.JwtResponse;
import com.anyview.dto.LoginRequest;
import com.anyview.dto.RegisterRequest;
import com.anyview.entity.School;
import com.anyview.entity.User;
import com.anyview.entity.UserRole;
import com.anyview.repository.SchoolRepository;
import com.anyview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final SessionService sessionService;

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication.getName());

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // 缓存用户信息到Redis
        sessionService.saveSession(String.valueOf(user.getId()), user);
        
        // 创建登录会话
        sessionService.createLoginSession(
            String.valueOf(user.getId()), 
            user.getUsername(), 
            request.getIp() != null ? request.getIp() : "unknown",
            request.getUserAgent() != null ? request.getUserAgent() : "unknown"
        );

        return new JwtResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getRealName()
        );
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
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
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
            
            // 先从Redis缓存获取用户信息
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                // 检查Redis中是否有缓存的用户信息
                Object cachedUser = sessionService.getSession(String.valueOf(user.getId()));
                if (cachedUser instanceof User) {
                    return (User) cachedUser;
                }
                // 如果Redis中没有，则缓存用户信息
                sessionService.saveSession(String.valueOf(user.getId()), user);
            }
            return user;
        }
        return null;
    }
}
