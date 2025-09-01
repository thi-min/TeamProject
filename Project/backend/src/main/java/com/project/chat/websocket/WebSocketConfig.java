package com.project.chat.websocket;

import com.project.common.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 모든 도메인 허용
                .withSockJS(); // SockJS 폴백 활성화

        log.info("WebSocket 엔드포인트 등록됨: /ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 구독 경로
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 경로
        log.info("메시지 브로커 설정 완료");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                // CONNECT 명령일 때만 인증 로직 수행
                if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
                    return message;
                }
                
                log.info("=== WebSocket 연결 시도 (CONNECT) ===");
                
                String authToken = accessor.getFirstNativeHeader("Authorization");
                
                if (authToken != null && authToken.startsWith("Bearer ")) {
                    String token = authToken.substring(7);
                    
                    try {
                        if (jwtTokenProvider.validateToken(token)) {
                            String memberId = jwtTokenProvider.getMemberIdFromToken(token);
                            String role = jwtTokenProvider.getRoleFromToken(token);
                            
                            log.info("토큰 검증 성공 - 사용자: {}, 역할: {}", memberId, role);
                            
                            List<SimpleGrantedAuthority> authorities = Arrays.asList(
                                new SimpleGrantedAuthority("ROLE_" + role)
                            );
                            
                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                memberId, null, authorities
                            );
                            
                            accessor.setUser(authentication);
                            log.info("인증 정보 설정 완료");
                        } else {
                            log.warn("JWT 토큰 검증 실패");
                            // 토큰이 유효하지 않으면 연결을 거부하거나 익명 처리
                            accessor.setSessionId(null);
                        }
                    } catch (Exception e) {
                        log.error("JWT 토큰 처리 중 오류: {}", e.getMessage());
                        accessor.setSessionId(null);
                    }
                } else {
                    log.info("JWT 토큰이 없어서 연결을 거부합니다. 또는 익명 사용자를 처리합니다.");
                    // 서비스 정책에 따라 토큰이 없는 연결은 거부
                    accessor.setSessionId(null);
                }
                
                return message;
            }
        });
    }
}