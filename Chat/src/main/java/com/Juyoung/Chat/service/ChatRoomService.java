package com.Juyoung.Chat.service;

import com.Juyoung.Chat.entity.ChatRoom;
import com.Juyoung.Chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /** 채팅방 생성 (중복 방지) */
    public ChatRoom createRoom(Long me, Long other) {

        if (me.equals(other)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

        return chatRoomRepository.findByUsers(me, other)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder()
                                .userAId(me)
                                .userBId(other)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }

    /** 참여자 검증 */
    public void validateParticipant(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        if (!room.getUserAId().equals(userId)
                && !room.getUserBId().equals(userId)) {
            throw new IllegalStateException("채팅방 참여자가 아님");
        }
    }
}