package com.project.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.chat.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {

}
