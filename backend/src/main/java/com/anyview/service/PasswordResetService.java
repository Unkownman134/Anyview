package com.anyview.service;

import com.anyview.dto.ForgotPasswordRequest;
import com.anyview.dto.ResetPasswordRequest;
import com.anyview.entity.PasswordResetToken;
import com.anyview.entity.User;
import com.anyview.repository.PasswordResetTokenRepository;
import com.anyview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        tokenRepository.deleteByUser(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(false);
        tokenRepository.save(token);

        System.out.println("重置密码链接: http://localhost:3002/reset-password?token=" + token.getToken());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("重置链接无效"));

        if (token.isExpired()) {
            throw new RuntimeException("重置链接已过期");
        }

        if (token.isUsed()) {
            throw new RuntimeException("重置链接已使用");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }
}
