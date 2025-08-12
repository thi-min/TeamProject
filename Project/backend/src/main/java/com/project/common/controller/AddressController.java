package com.project.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.dto.JusoSearchResponse;
import com.project.common.service.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
//@CrossOrigin(origins = {"http://localhost:3000"}) // 필요 시 조정
public class AddressController {
	private final AddressService addressService;
	
	//주소 자동완성/검색 엔드포인트
	//프론트는 해당 주소만 호출
	//GET /api/addresses/search?keyword=서울 강남대로&page=1&size=10
	@GetMapping("/adds_earch")
	public ResponseEntity<JusoSearchResponse>serarch(
			@RequestParam String keyword,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size){
		//간단검증
		if(keyword == null || keyword.trim().length()<2) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(addressService.search(keyword.trim(), page, size));
	}
}
