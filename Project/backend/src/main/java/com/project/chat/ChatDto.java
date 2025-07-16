package com.project.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

	private Integer manageNum;
	
	private String chatCont;
	
	private String sendTime;
	
	private String chatCheck;
	
}
