package com.Juyoung.Chat.service;

import com.Juyoung.Chat.dto.auth.LoginRequest;
import com.Juyoung.Chat.dto.auth.RegisterRequest;
import com.Juyoung.Chat.entity.User;
import com.Juyoung.Chat.global.jwt.JwtTokenProvider;
import com.Juyoung.Chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** 회원가입 */
    public void register(RegisterRequest req) {
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(user);
    }

    /** 로그인 */
    public String login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getId())
                .or(() -> userRepository.findByEmail(req.getId()))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        return jwtTokenProvider.createToken(user.getId());
    }
}