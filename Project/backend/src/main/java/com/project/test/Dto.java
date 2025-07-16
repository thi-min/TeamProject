package com.project.test;

public class Dto {
	//예시코드
	//요청용
//	package com.shop.dto;
//
//	import lombok.Getter;
//	import lombok.Setter;
//
//	@Getter
//	@Setter
//	public class ItemDto {
//	    private String itemName;
//	    private int price;
//	    private String itemDetail;
//	}
	
	//응답용
//	package com.shop.dto;
//
//	import lombok.AllArgsConstructor;
//	import lombok.Getter;
//
//	@Getter
//	@AllArgsConstructor
//	public class ItemResponseDto {
//	    private Long id;
//	    private int price;
//	    private String itemName;
//	}

}

//요청과 응답에서 필요한 데이터만 담기 위해 사용하는 클래스
//예시
//1. 사용자가 폼 입력 → 컨트롤러로 요청
//2. ItemDto에 값이 바인딩됨
//3. ItemDto → Item 엔티티로 변환
//4. DB 저장

//주요 에너테이션
//@Getter, @Setter  
//→ 모든 필드에 Getter/Setter 자동 생성  
//@NoArgsConstructor  
//→ 기본 생성자 자동 생성  
//@AllArgsConstructor  
//→ 모든 필드를 매개변수로 받는 생성자 자동 생성  
//@Builder  
//→ 빌더 패턴을 통해 객체 생성 메서드 자동 생성  
//@ToString  
//→ toString() 메서드 자동 생성  
//@Data  
//→ @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor 포함  
//@Valid  
//→ Controller에서 유효성 검증 트리거 역할 (@RequestBody, @ModelAttribute 등과 함께 사용)  
//@NotNull, @NotEmpty, @Size, @Email 등  
//→ DTO 필드에 대한 유효성 검사 지정  
//@JsonProperty  
//→ JSON 필드명과 자바 필드명을 매핑할 때 사용  
//@JsonIgnore  
//→ 해당 필드를 JSON 직렬화/역직렬화에서 제외  


