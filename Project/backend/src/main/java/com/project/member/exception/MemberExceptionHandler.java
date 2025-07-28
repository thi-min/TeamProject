package com.project.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//member안에서 일어나는 모든 예외처리
@RestControllerAdvice(basePackages = "com.project.member")
public class MemberExceptionHandler {
	
	//아이디 중복 등 IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
//    회원가입	"이미 존재하는 아이디 입니다."
//    로그인	"아이디 또는 비밀번호가 일치하지 않습니다."
//    마이페이지 조회/수정	"존재하지 않는 회원입니다."
//    SMS 수신 동의	"회원 계정에 문제가 발생했습니다."
//    회원탈퇴	"해당 회원이 존재하지 않습니다."
//    아이디 찾기	"일치하는 아이디가 없습니다."
//    비밀번호 찾기	"입력하신 정보와 일치하는 회원이 없습니다."
//    비밀번호 변경	"회원 없음", "현재 비밀번호가 일치하지 않습니다.", 등
//    휴대폰 중복확인	"이미 가입된 휴대폰 번호입니다."
}
