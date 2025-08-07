package com.project.common.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.salt.ZeroSaltGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//Jasypt 암호화 유틸 클래스
//전화번호 등 개인정보를 암호화 및 복호화할 때 사용
//Spring 환경에서 암호화 키는 application.properties 또는 환경변수에서 주입
@Component
public class JasyptUtil {

	private static final String SECRET_KEY = "test-key";
	private static final StandardPBEStringEncryptor encryptor;
	
    //application.properties에서 암호화 키를 주입받음
    @Value("${JASYPT_ENCRYPTOR_PASSWORD}")
    private String secretKey;

    //암호화 설정이 동일한 encryptor 생성 메서드
    //복호화 실패 방지를 위해 salt, iv, 개정등을 고정시킴
    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);                         // 암호화 키 설정
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());       // salt 고정
        encryptor.setIvGenerator(new NoIvGenerator());             // IV 고정
    }

    //문자열을 암호화하는 메서드
    //@param plainText 사용자가 입력한 원본 텍스트
    //@return 암호화된 문자열 (Base64 인코딩된 형태)
    public static String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    //암호화된 문자열을 복호화하는 메서드
    //@param encryptedText 암호화된 문자열
    //@return 원본 문자열
    public static String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
