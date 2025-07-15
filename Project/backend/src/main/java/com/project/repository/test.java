package com.project.repository;

public class test {

	//코드예시
//	package com.shop.repository;
//
//	import com.shop.entity.Item;
//	import org.springframework.data.jpa.repository.JpaRepository;
//	import java.util.List;
//
//	public interface ItemRepository extends JpaRepository<Item, Long> {
//	    	Item은 엔티티 클래스, Long은 기본 키 타입
//	    // 이름으로 검색
//	    List<Item> findByItemName(String itemName);
//
//	    // 가격보다 높은 상품 찾기
//	    List<Item> findByPriceGreaterThan(int price);
//	}

}

//설명
//데이터베이스와 직접 통신하는 계층
//Entity 객체를 저장, 조회, 수정, 삭제 하는 메서드를 가지고 있음
//주로 JPA 혹은 MyBatis와 함께 사용됨

//참고 흐름
//사용자 → Controller → Service → Repository(DB접근) > DB

//주요 에너테이션
//@Repository  
//→ 해당 클래스가 데이터 접근 계층(DAO)임을 나타내며, 예외를 Spring이 처리할 수 있게 함  
//@EnableJpaRepositories  
//→ JPA 기반의 리포지토리 인터페이스들을 스캔하고 자동으로 빈 등록  
//@PersistenceContext  
//→ JPA의 EntityManager를 주입받을 때 사용  
//@Query  
//→ JPQL 또는 네이티브 쿼리를 직접 작성할 때 사용  
//@Modifying  
//→ @Query로 작성된 쿼리가 INSERT, UPDATE, DELETE일 경우 반드시 함께 사용  
//@Transactional  
//→ 트랜잭션 처리를 위한 애너테이션 (주로 Service에 사용되지만 Repository에도 가능)  
//@Param  
//→ @Query 내 파라미터를 바인딩할 때 이름을 명시적으로 지정  
//@NoRepositoryBean  
//→ 공통 레포지토리 인터페이스에서 상속만 허용하고 직접 사용되지 않도록 설정  
//@Lock  
//→ JPA에서 비관적/낙관적 락 설정을 지정할 때 사용  
//@DynamicUpdate  
//→ 변경된 필드만 update 쿼리에 포함되도록 설정 (Hibernate 전용)  
