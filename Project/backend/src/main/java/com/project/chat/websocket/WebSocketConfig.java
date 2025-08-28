package com.project.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:3000") // 특정 Origin 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*"); // 모든 헤더 허용
    }
    /**
     * 클라이언트가 WebSocket 서버에 연결할 엔드포인트를 등록합니다.
     * SockJS를 사용하여 WebSocket을 지원하지 않는 브라우저에서도 연결할 수 있도록 합니다.
     * @param registry STOMP 엔드포인트 등록 관리자
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 모든 Origin에서 접근 허용
                .withSockJS(); // SockJS 지원 추가
    }

    /**
     * 인메모리 메시지 브로커를 설정합니다.
     * @param registry 메시지 브로커 등록 관리자
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 클라이언트에게 메시지를 전달할 destination(prefix) 설정
        // /sub/chat/room/{roomNum} 형태의 구독을 허용
        registry.enableSimpleBroker("/sub");

        // 클라이언트가 서버로 메시지를 보낼 때 사용할 destination(prefix) 설정
        // /pub/chat/message 형태의 메시지 전송을 허용
        registry.setApplicationDestinationPrefixes("/pub");
    }
}