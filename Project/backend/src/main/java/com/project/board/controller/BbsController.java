package com.project.board.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

 // 게시글 작성 (관리자 or 회원 ID 필요)
    @PostMapping("/write")
    public BbsDto write(
        @RequestBody BbsDto dto,
        @RequestParam(required = false) Long memberNum,
        @RequestParam(required = false) Long adminId
    ) {
        return bbsService.createBbs(dto, memberNum, adminId);
    }


    // 게시글 조회
    @GetMapping("/{id}")
    public BbsDto get(@PathVariable Long id) {
        return bbsService.getBbs(id);
    }

    // 게시글 타입별 조회
    @GetMapping("/type/{type}")
    public List<BbsDto> getByType(@PathVariable BoardType type) {
        return bbsService.getAllByType(type);
    }

    // QNA 저장
    @PostMapping("/{bbsId}/qna")
    public QandADto saveQna(@PathVariable Long bbsId, @RequestBody QandADto dto,  @RequestParam Long requesterAdminId) {
    	return bbsService.saveQna(bbsId, dto, requesterAdminId);
    }

    // QNA 조회
    @GetMapping("/{bbsId}/qna")
    public QandADto getQna(@PathVariable Long bbsId) {
        return bbsService.getQna(bbsId);
    }

    // QNA 개별 수정
    @PutMapping("/qna/{qnaId}")
    public ResponseEntity<?> updateQna(@PathVariable Long qnaId, @RequestBody QandADto dto) {
        bbsService.updateQna(qnaId, dto);
        return ResponseEntity.ok("QnA 수정 완료");
    }

    // QNA 개별 삭제
    @DeleteMapping("/qna/{qnaId}")
    public ResponseEntity<?> deleteQna(@PathVariable Long qnaId) {
        bbsService.deleteQna(qnaId);
        return ResponseEntity.ok("QnA 삭제 완료");
    }

    // 이미지 업로드
    @PostMapping("/{bbsId}/images")
    public List<ImageBbsDto> uploadImages(@PathVariable Long bbsId, @RequestBody List<ImageBbsDto> dtos) {
        return bbsService.saveImageBbsList(bbsId, dtos);
    }

    // 이미지 조회
    @GetMapping("/{bbsId}/images")
    public List<ImageBbsDto> getImages(@PathVariable Long bbsId) {
        return bbsService.getImageBbsList(bbsId);
    }

    // 이미지 개별 수정
    @PutMapping("/image/{imageId}")
    public ResponseEntity<?> updateImage(@PathVariable Long imageId, @RequestBody ImageBbsDto dto) {
        bbsService.updateImage(imageId, dto);
        return ResponseEntity.ok("이미지 수정 완료");
    }

    // 이미지 개별 삭제
    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        bbsService.deleteImage(imageId);
        return ResponseEntity.ok("이미지 삭제 완료");
    }

    // 파일 업로드
    @PostMapping("/{bbsId}/files")
    public List<FileUpLoadDto> uploadFiles(@PathVariable Long bbsId, @RequestBody List<FileUpLoadDto> dtos) {
        return bbsService.saveFileList(bbsId, dtos);
    }

    // 파일 조회
    @GetMapping("/{bbsId}/files")
    public List<FileUpLoadDto> getFiles(@PathVariable Long bbsId) {
        return bbsService.getFilesByBbs(bbsId);
    }

    // 파일 개별 수정
    @PutMapping("/file/{fileId}")
    public ResponseEntity<?> updateFile(@PathVariable Long fileId, @RequestBody FileUpLoadDto dto) {
        bbsService.updateFile(fileId, dto);
        return ResponseEntity.ok("파일 수정 완료");
    }

    // 파일 개별 삭제
    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        bbsService.deleteFile(fileId);
        return ResponseEntity.ok("파일 삭제 완료");
    }

    // 게시글 삭제 (관리자 또는 회원 권한 체크 포함)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePosts(
        @RequestBody List<Long> bbsIds,
        @RequestParam(required = false) Long adminId,
        @RequestParam(required = false) Long memberNum
    ) {
        bbsService.deleteBbs(bbsIds, adminId, memberNum);
        return ResponseEntity.ok("게시글 삭제 완료");
    }
    // 게시글 수정
    @PutMapping("/{bbsId}")
    public BbsDto updatePost(
        @PathVariable Long bbsId,
        @RequestBody BbsDto dto,
        @RequestParam Long memberNum
    ) {
        return bbsService.updateBbs(bbsId, dto, memberNum);
    }

    // 페이징 조회
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
