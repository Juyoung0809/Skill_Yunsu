package com.Juyoung.Chat.repository;

import com.Juyoung.Chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUserAIdAndUserBId(Long userAId, Long userBId);

    default Optional<ChatRoom> findByUsers(Long user1, Long user2) {
        return findByUserAIdAndUserBId(user1, user2)
                .or(() -> findByUserAIdAndUserBId(user2, user1));
    }

    List<ChatRoom> findByUserAIdOrUserBId(Long userAId, Long userBId);
}