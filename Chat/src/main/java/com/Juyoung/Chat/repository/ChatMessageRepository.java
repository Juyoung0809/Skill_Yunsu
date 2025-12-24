package com.Juyoung.Chat.repository;

import com.Juyoung.Chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

    Optional<ChatMessage> findTopByRoomIdOrderBySentAtDesc(Long roomId);
}