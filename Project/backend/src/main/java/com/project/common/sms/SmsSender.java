package com.project.common.sms;

/**
 * SMS 발송 포트(인터페이스)
 * - 실제 운영에서는 CoolSMS, Nurigo, Naver SENS, Twilio 등 구현체를 추가
 */
public interface SmsSender {
    /**
     * @param from 발신번호(관리자 번호)
     * @param to   수신번호(회원 번호)
     * @param text 본문
     */
    void send(String from, String to, String text);
}
