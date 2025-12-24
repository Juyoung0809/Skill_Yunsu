package com.Juyoung.Chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListResponse {

    private Long roomId;
    private Long otherUserId;
    private String lastMessage;
    private LocalDateTime lastSentAt;
}