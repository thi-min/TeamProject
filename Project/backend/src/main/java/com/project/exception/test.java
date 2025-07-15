package com.project.exception;

public class test {
	//코드예시
//	package com.shop.exception;
//
//	public class ItemNotFoundException extends RuntimeException {
//	    public ItemNotFoundException(String message) {
//	        super(message);
//	    }
//	}

}

//설명
//실행중 발생할 수 있는 예외상황을 직접 정의하고 처리하기 위해 사용되는 폴더 파일

//주요 에너테이션
//@ControllerAdvice  
//→ 전역 예외 처리 클래스로 지정 (모든 컨트롤러의 예외를 감지)  
//@RestControllerAdvice  
//→ @ControllerAdvice + @ResponseBody (예외를 JSON 형식으로 응답)  
//@ExceptionHandler  
//→ 특정 예외 발생 시 실행할 메서드 지정  
//@ResponseStatus  
//→ 예외 발생 시 HTTP 상태 코드 지정  
//@Valid  
//→ DTO 유효성 검증 트리거 (예외 발생 시 BindException 또는 MethodArgumentNotValidException 발생)  
//@ModelAttribute  
//→ 유효성 검증 대상 객체를 컨트롤러에 바인딩할 때 사용 (폼 기반 요청)  
//@InitBinder  
//→ 유효성 검증 또는 바인딩 전처리를 위한 커스터마이징 메서드 등록  
//@Constraint  
//→ 커스텀 유효성 검사 애너테이션 정의 시 사용하는 메타 애너테이션  
//@Validated  
//→ 클래스 단위에서 유효성 검사를 활성화 (서비스나 컨트롤러 단에서 사용 가능)  
