package com.project.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//admin안에서 일어나는 모든 예외처리
@RestControllerAdvice(basePackages = "com.project.admin")
public class AdminExceptionHandler {
//	
//	// ✅ IllegalArgumentException 처리
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }
    // ✅ 보안 예외 (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다. 관리자만 접근 가능합니다.");
    }
    // ✅ 기타 예외 (일반 400 처리 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body("잘못된 요청: " + ex.getMessage());
    }
    // ✅ 예상치 못한 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
    }
//    회원가입	"이미 존재하는 아이디 입니다."
//    로그인	"아이디 또는 비밀번호가 일치하지 않습니다."
//    마이페이지/수정	"존재하지 않는 회원입니다."
//    회원탈퇴	"해당 회원이 존재하지 않습니다."
//    아이디 찾기	"일치하는 아이디가 없습니다."
//    비밀번호 찾기	"입력하신 정보와 일치하는 회원이 없습니다."
//    비밀번호 변경	여러 조건에 대해 IllegalArgumentException
}
