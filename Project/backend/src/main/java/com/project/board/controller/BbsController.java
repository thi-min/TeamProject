package com.project.board.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.dto.QandADto;
import com.project.board.service.BbsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bbs")
@RequiredArgsConstructor
public class BbsController {

    private final BbsService bbsService;

    //게시글 작성
    @PostMapping("/write")
    public BbsDto write(@RequestBody BbsDto dto) {
        return bbsService.createBbs(dto);
    }

    //게시글 조회
    @GetMapping("/{id}")
    public BbsDto get(@PathVariable Long id) {
        return bbsService.getBbs(id);
    }

    //게시글 타입별 조회
    @GetMapping("/type/{type}")
    public List<BbsDto> getByType(@PathVariable BoardType type) {
        return bbsService.getAllByType(type);
    }

    //QNA 저장
    @PostMapping("/{bbsId}/qna")
    public QandADto saveQna(@PathVariable Long bbsId, @RequestBody QandADto dto) {
        return bbsService.saveQna(bbsId, dto);
    }

    //QNA 조회
    @GetMapping("/{bbsId}/qna")
    public QandADto getQna(@PathVariable Long bbsId) {
        return bbsService.getQna(bbsId);
    }

    //이미지 업로드
    @PostMapping("/{bbsId}/images")
    public List<ImageBbsDto> uploadImages(@PathVariable Long bbsId, @RequestBody List<ImageBbsDto> dtos) {
        return bbsService.saveImageBbsList(bbsId, dtos);
    }

    //이미지 조회
    @GetMapping("/{bbsId}/images")
    public List<ImageBbsDto> getImages(@PathVariable Long bbsId) {
        return bbsService.getImageBbsList(bbsId);
    }

    //파일 업로드
    @PostMapping("/{bbsId}/files")
    public List<FileUpLoadDto> uploadFiles(@PathVariable Long bbsId, @RequestBody List<FileUpLoadDto> dtos) {
        return bbsService.saveFileList(bbsId, dtos);
    }

    //파일 조회
    @GetMapping("/{bbsId}/files")
    public List<FileUpLoadDto> getFiles(@PathVariable Long bbsId) {
        return bbsService.getFilesByBbs(bbsId);
    }
    
    // 게시글 삭제 (체크박스 선택된 게시글들 삭제)
    @DeleteMapping("/delete")
    public void deletePosts(@RequestBody List<Long> bbsIds) {
        bbsService.deleteBbsByIds(bbsIds);
    }

    // 게시글 수정 (본문, 첨부파일 수정 포함)
    @PutMapping("/{bbsId}")
    public BbsDto updatePost(@PathVariable Long bbsId, @RequestBody BbsDto dto) {
        return bbsService.updateBbs(bbsId, dto);
    }

    // 페이징 조회 (최신순, 조회순 정렬 지원)
    @GetMapping("/list")
    public Page<BbsDto> getPagedPosts(
        @RequestParam(name = "type", required = false) BoardType type,
        @RequestParam(name = "sort", defaultValue = "latest") String sort,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return bbsService.getPagedPosts(type, sort, pageable);
    }

    // 게시글 검색
    @GetMapping("/search")
    public Page<BbsDto> searchPosts(
    		@RequestParam("keyword") String keyword,
            @RequestParam(name = "searchType", required = false, defaultValue = "title") String searchType,
            @RequestParam(name = "type", required = false) BoardType type,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return bbsService.searchPosts(searchType, keyword, type, pageable);
    }
}