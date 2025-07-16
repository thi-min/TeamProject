package com.project.test;

public class Entity {
	//코드예시
	//맴버
//	@Entity
//	@Table(name = "member")
//	public class Member {
//	    @Id
//	    @GeneratedValue(strategy = GenerationType.IDENTITY)
//	    private Long id;
//
//	    private String email;
//	    private String name;
//	    private String password;
//
//		public Long getId() {
//	        return id;
//	    }
//	
//	    public void setId(Long id) {
//	        this.id = id;
//	    }
//		Getter/Setter 생략
//	}
}

//entity <-> DB
//실제 DB테이블과 매핑되는 클래스
//데이터 베이스에서 관리할 정보(회원, 상품, 주문 등) 자바 객체로 만드는것

//주요 에너테이션
//@Entity  
//→ 해당 클래스가 DB 테이블과 매핑되는 JPA 엔티티임을 나타냄  
//@Table(name = "...")  
//→ 테이블 이름 지정 (기본은 클래스명과 동일)  
//@Id  
//→ 기본 키(PK)로 사용할 필드 지정  
//@GeneratedValue  
//→ 기본 키 자동 생성 전략 설정 (e.g. IDENTITY, AUTO)  
//@Column  
//→ DB 컬럼 속성 지정 (nullable, length 등 조정 가능)  
//@Lob  
//→ CLOB/BLOB 등 대용량 데이터 컬럼 매핑  
//@Enumerated(EnumType.STRING)  
//→ Enum 타입을 문자열로 DB에 저장  
//@Temporal  
//→ Date 타입의 저장 방식 지정 (DATE, TIME, TIMESTAMP 중 선택)  
//@OneToMany  
//→ 1:N 관계 설정 (엔티티 하나가 여러 개를 가질 때)  
//@ManyToOne  
//→ N:1 관계 설정 (여러 엔티티가 하나에 속할 때)  
//@OneToOne  
//→ 1:1 관계 설정 (서로 하나씩 매핑될 때)  
//@ManyToMany  
//→ N:N 관계 설정 (중간 테이블 필요)  
//@JoinColumn  
//→ 외래 키(FK) 컬럼 정의 (연결되는 테이블의 컬럼 지정)  
//@JoinTable  
//→ 다대다 관계에서 중간 테이블 및 연결 컬럼 정의  
//@Transient  
//→ DB에 매핑되지 않도록 제외하는 필드 지정  
//@PrePersist  
//→ 엔티티 저장 전(INSERT 전)에 실행할 메서드 지정  
//@PreUpdate  
//→ 엔티티 수정 전(UPDATE 전)에 실행할 메서드 지정  
