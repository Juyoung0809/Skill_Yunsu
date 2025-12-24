package com.Juyoung.Chat.controller;

import com.Juyoung.Chat.dto.chat.ChatMessageRequest;
import com.Juyoung.Chat.dto.chat.ChatMessageResponse;
import com.Juyoung.Chat.dto.chat.ChatRoomListResponse;
import com.Juyoung.Chat.entity.ChatMessage;
import com.Juyoung.Chat.entity.ChatRoom;
import com.Juyoung.Chat.repository.ChatMessageRepository;
import com.Juyoung.Chat.service.ChatRoomService;
import com.Juyoung.Chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Chat", description = "채팅 (REST + WebSocket)")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // =====================================================
    // 채팅방 생성 (REST)
    // =====================================================
    @Operation(summary = "1:1 채팅방 생성 (중복 방지)")
    @PostMapping("/rooms/{otherUserId}")
    @ResponseBody
    public ChatRoom createRoom(
            @AuthenticationPrincipal Long myId,
            @PathVariable Long otherUserId
    ) {
        return chatRoomService.createRoom(myId, otherUserId);
    }

    // =====================================================
    // 채팅 메시지 조회 (REST)
    // =====================================================
    @Operation(summary = "채팅 메시지 조회")
    @GetMapping("/rooms/{roomId}/messages")
    @ResponseBody
    public List<ChatMessage> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Long userId
    ) {
        chatRoomService.validateParticipant(roomId, userId);
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    // =====================================================
    // 채팅 메시지 전송 (WebSocket) - ⭐ 핵심 수정
    // =====================================================
    @MessageMapping("/chat/send")
    public void sendMessage(
            ChatMessageRequest req,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // WebSocket에서는 Principal을 이렇게 꺼내야 함
        Principal principal = headerAccessor.getUser();

        if (principal == null) {
            throw new IllegalStateException("WebSocket Principal is null");
        }

        Long senderId = Long.valueOf(principal.getName());

        // 채팅방 참여자 검증
        chatRoomService.validateParticipant(req.getRoomId(), senderId);

        // 메시지 생성 + DB 저장
        ChatMessageResponse response =
                chatService.createAndSaveMessage(senderId, req);

        // 채팅방 구독자에게 브로드캐스트
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + req.getRoomId(),
                response
        );
    }

    // =====================================================
    // 내 채팅방 목록 조회 (REST)
    // =====================================================
    @Operation(summary = "내 채팅방 목록 조회")
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomListResponse> getMyChatRooms(
            @AuthenticationPrincipal Long userId
    ) {
        return chatService.getMyChatRooms(userId);
    }
}