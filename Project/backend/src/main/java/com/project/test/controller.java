package com.project.test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controller {

// 요청수신, 로직호출, 결과응답
// 주문 관련 요청 처리
		
// 사용자가 웹페이지나 API를 통해 보내는 요청을 받아서 처리하고 응답을 반환하는 역활
		
	//아래 예시
//	@GetMapping("/items")
//	public String itemList(Model model) {
//	    List<Item> items = itemService.getItemList();
//	    model.addAttribute("items", items);
//	    return "item/list";  // 뷰 이름
//	}
	
}
//@Controller, @RestController
//@Controller: 스프링이 이 클래스를 웹 컨트롤러로 인식하게 함
//@GetMapping("/items"): /items 경로로 들어오는 GET 요청을 처리
//Model: 뷰(View)에 데이터를 전달

//참고 흐름
//사용자 → Controller → Service → Repository(DB접근) > DB

//주요 에너테이션
//@Controller	뷰 반환용 컨트롤러
//@RestController	JSON 응답 전용 컨트롤러
//@RequestMapping	클래스/메서드에 공통 URL 설정
//@GetMapping 등	HTTP 메서드 매핑 (GET/POST/PUT/DELETE 등)
//@RequestParam	쿼리 파라미터 매핑
//@PathVariable	URL 경로 매핑
//@RequestBody	JSON → 객체 변환
//@ModelAttribute	폼 데이터 바인딩
//@ResponseBody	반환값을 JSON 등으로 출력