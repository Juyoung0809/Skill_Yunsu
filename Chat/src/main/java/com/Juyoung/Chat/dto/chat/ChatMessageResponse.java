package com.Juyoung.Chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long roomId;
    private Long senderId;
    private String message;
    private LocalDateTime sentAt;
}