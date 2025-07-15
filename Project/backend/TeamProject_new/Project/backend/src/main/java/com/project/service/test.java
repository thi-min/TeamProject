package com.project.service;

public class test {

}

//@Service
//컨트롤러(Controller)와 저장소(Repository) 사이의 중간 처리 담당자
//비즈니스 로직을 수행하고, 필요한 데이터를 가공하거나 여러 저장소를 묶어서 처리
//여러 Repository 호출 가능
//트랜잭션 관리 (@Transactional)

//참고 흐름
//사용자 → Controller → Service → Repository(DB접근) > DB

//주요 에너테이션
//@Service  
//→ 해당 클래스가 비즈니스 로직을 처리하는 서비스 계층임을 나타내며, 자동으로 Spring Bean으로 등록됨  
//@Transactional  
//→ 메서드 또는 클래스 단위에서 트랜잭션을 적용하여 일괄 처리/롤백이 가능하도록 설정  
//@Autowired  
//→ 필요한 의존 객체(Repository 등)를 자동으로 주입  
//@RequiredArgsConstructor  
//→ final 필드를 생성자 주입 방식으로 자동 초기화 (의존성 주입 + 불변성 유지)  
//@Async  
//→ 비동기 작업 실행을 위해 메서드를 별도 스레드에서 실행 (멀티스레드 처리)  
//@Scheduled  
//→ 일정 주기마다 실행되는 작업(스케줄링)을 정의 (cron 표현식 등 사용 가능)  
//@Value  
//→ application.yml 등 외부 설정 파일에서 값을 주입받을 때 사용  
//@Cacheable  
//→ 해당 메서드의 결과를 캐시하여 성능을 최적화 (메모리/Redis 등과 연동 가능)  
//@PreAuthorize  
//→ 메서드 호출 전에 권한을 검사 (Spring Security와 함께 사용)  
//@PostConstruct  
//→ 의존성 주입이 완료된 후 초기화 로직 실행 시 사용  
