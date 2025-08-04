package com.project.common.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.salt.ZeroSaltGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 🔐 Jasypt 암호화 유틸 클래스
 * - 전화번호 등 개인정보를 암호화 및 복호화할 때 사용
 * - Spring 환경에서 암호화 키는 application.properties 또는 환경변수에서 주입
 */
@Component
public class JasyptUtil {

	private static final String SECRET_KEY = "test-key";
	private static final StandardPBEStringEncryptor encryptor;
	
    // 🔐 암호화 키 (환경변수 또는 properties에서 주입)
    @Value("${JASYPT_ENCRYPTOR_PASSWORD}")
    private String secretKey;

    // 🔐 키 유효성 검사
    private void validateKey() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Jasypt 암호화 키가 설정되지 않았습니다.");
        }
    }

    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());
        encryptor.setIvGenerator(new NoIvGenerator());
    }
    
    // 🔐 암호화 설정이 동일한 encryptor 생성 메서드
    private StandardPBEStringEncryptor createEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(secretKey);                         // 암호화 키 설정
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());       // salt 고정
        encryptor.setIvGenerator(new NoIvGenerator());             // IV 고정
        return encryptor;
    }

    // 문자열을 암호화하는 메서드
    // @param plainText 사용자가 입력한 원본 텍스트
    // @return 암호화된 문자열 (Base64 인코딩된 형태)
    public static String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    // 암호화된 문자열을 복호화하는 메서드
    // @param encryptedText 암호화된 문자열
    // @return 원본 문자열
    public static String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
