package com.Juyoung.Chat.service;

import com.Juyoung.Chat.dto.chat.ChatMessageRequest;
import com.Juyoung.Chat.dto.chat.ChatMessageResponse;
import com.Juyoung.Chat.dto.chat.ChatRoomListResponse;
import com.Juyoung.Chat.entity.ChatMessage;
import com.Juyoung.Chat.entity.ChatRoom;
import com.Juyoung.Chat.repository.ChatMessageRepository;
import com.Juyoung.Chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    /** 메시지 생성 + DB 저장 */
    public ChatMessageResponse createAndSaveMessage(
            Long senderId,
            ChatMessageRequest req
    ) {
        ChatMessage message = ChatMessage.builder()
                .roomId(req.getRoomId())
                .senderId(senderId)
                .message(req.getMessage())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        return new ChatMessageResponse(
                message.getRoomId(),
                message.getSenderId(),
                message.getMessage(),
                message.getSentAt()
        );
    }

    /** 채팅방 목록 조회 */
    public List<ChatRoomListResponse> getMyChatRooms(Long myId) {

        List<ChatRoom> rooms =
                chatRoomRepository.findByUserAIdOrUserBId(myId, myId);

        return rooms.stream().map(room -> {

            Long otherUserId =
                    room.getUserAId().equals(myId)
                            ? room.getUserBId()
                            : room.getUserAId();

            ChatMessage lastMessage =
                    chatMessageRepository
                            .findTopByRoomIdOrderBySentAtDesc(room.getId())
                            .orElse(null);

            return new ChatRoomListResponse(
                    room.getId(),
                    otherUserId,
                    lastMessage != null ? lastMessage.getMessage() : null,
                    lastMessage != null ? lastMessage.getSentAt() : null
            );
        }).toList();
    }
}