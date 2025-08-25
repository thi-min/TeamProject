package com.project.common.sms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.project.common.sms.SmsSender;

/**
 * 임시 구현체: 실제 SMS 대신 로그로 출력
 * - 나중에 실 구현체로 교체하면 됨
 */
@Component
public class ConsoleSmsSender implements SmsSender {
    private static final Logger log = LoggerFactory.getLogger(ConsoleSmsSender.class);

    @Override
    public void send(String from, String to, String text) {
        log.info("[SMS][MOCK] FROM: {} -> TO: {} | BODY: {}", from, to, text);
    }
}