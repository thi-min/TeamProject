package com.project.common.util;

import java.util.Optional;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

public class JasyptUtil {
	private static final String ENC_KEY;
	static {
	    ENC_KEY = System.getProperty("JASYPT_ENCRYPTOR_PASSWORD");
	    System.out.println("🔐 Jasypt 키 확인: " + ENC_KEY);
	}
	
    //환경변수로부터 암호화 키를 읽어옴 (보안상 코드에 직접 작성하지 않음)
    private static final String SECRET_KEY =
            Optional.ofNullable(System.getProperty("JASYPT_ENCRYPTOR_PASSWORD")) // 테스트 시 사용
                    .orElse(System.getenv("JASYPT_ENCRYPTOR_PASSWORD"));         // 운영/개발 환경

    //문자열을 암호화하는 메서드
    //@param plainText 사용자가 입력한 원본 텍스트
    //@return 암호화된 문자열 (Base64 인코딩된 형태)
    public static String encrypt(String plainText) {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("Jasypt 암호화 키가 설정되지 않았습니다.");
        }

        // 🔧 수정된 부분 (고정된 암호문 생성을 위해 설정 추가)
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);  // 암호화 키 설정
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());     // salt 고정
        encryptor.setIvGenerator(new NoIvGenerator());           // IV 고정

        return encryptor.encrypt(plainText);    // 암호화 실행
    }

    //암호화된 문자열을 복호화하는 메서드
    //@param encryptedText 암호화된 문자열
    //@return 원본 문자열
    public static String decrypt(String encryptedText) {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("Jasypt 암호화 키가 설정되지 않았습니다.");
        }

        // 🔧 수정된 부분 (암호화 설정과 동일해야 복호화 가능)
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());
        encryptor.setIvGenerator(new NoIvGenerator());

        return encryptor.decrypt(encryptedText);    // 복호화 실행
    }
}
