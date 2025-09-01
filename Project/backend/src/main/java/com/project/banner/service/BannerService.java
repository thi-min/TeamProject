package com.project.banner.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project.banner.dto.BannerRequestDto;
import com.project.banner.dto.BannerResponseDto;

public interface BannerService {
	//배너 생성
	void createBanner(BannerRequestDto dto, MultipartFile file) throws IOException;
	//모든 배너 리스트 조회
    List<BannerResponseDto> getAll();
    //상세보기
    BannerResponseDto getDetail(Long id);
    //배너 수정
    void update(Long id, BannerRequestDto dto, MultipartFile file) throws IOException;
    //배너 단건 삭제
    void delete(Long id);
    //배너 복수 삭제
    void deleteBulk(List<Long> ids);
    
    //25.09.01 안형주 추가
    //활성상태 베너 조회
    List<BannerResponseDto> getActiveBanners();
}