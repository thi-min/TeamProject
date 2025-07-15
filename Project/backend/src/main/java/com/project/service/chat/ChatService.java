package com.project.service.chat;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.entity.chat.ChatEntity;
import com.project.repository.chat.ChatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	
	public void saveChat(ChatEntity entity) {
		chatRepository.save(entity);
	}
	
	public List<ChatEntity> getAllChat(){
		return chatRepository.findAll();
	}
}
