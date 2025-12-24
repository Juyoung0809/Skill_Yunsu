package com.Juyoung.Chat.controller;

import com.Juyoung.Chat.dto.auth.AuthResponse;
import com.Juyoung.Chat.dto.auth.LoginRequest;
import com.Juyoung.Chat.dto.auth.RegisterRequest;
import com.Juyoung.Chat.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입 / 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "username / email / password로 회원 가입"
    )
    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest req) {
        authService.register(req);
    }

    @Operation(
            summary = "로그인",
            description = "username 또는 email + password로 로그인 후 JWT 발급"
    )
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return new AuthResponse(token);
    }
}