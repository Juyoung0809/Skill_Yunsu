package com.Juyoung.Chat.global.jwt;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompJwtInterceptor implements ChannelInterceptor {

    private static final String WS_USER_ID = "WS_USER_ID";

    private final JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    public void init() {
        System.out.println("### StompJwtInterceptor loaded ###");
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // ===============================
        // CONNECT: JWT 검증 + 세션에 저장
        // ===============================
        if (StompCommand.CONNECT.equals(command)) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("WebSocket Authorization 헤더 없음");
            }

            String token = authHeader.substring(7);
            Long userId = jwtTokenProvider.getUserId(token);

            // 세션 속성에 저장
            Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
            if (sessionAttrs != null) {
                sessionAttrs.put(WS_USER_ID, userId);
            }

            // CONNECT 프레임에도 Principal 설정
            accessor.setUser(
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(),
                            null,
                            List.of()
                    )
            );
        }

        // ===============================
        // SEND: 세션에서 꺼내 Principal 재설정
        // ===============================
        if (StompCommand.SEND.equals(command)) {

            if (accessor.getUser() == null) {
                Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
                if (sessionAttrs != null && sessionAttrs.containsKey(WS_USER_ID)) {

                    Long userId = (Long) sessionAttrs.get(WS_USER_ID);

                    accessor.setUser(
                            new UsernamePasswordAuthenticationToken(
                                    userId.toString(),
                                    null,
                                    List.of()
                            )
                    );
                }
            }
        }

        return message;
    }
}