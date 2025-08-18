package com.project.board.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.BoardType;
import com.project.board.dto.*;
import com.project.board.service.BbsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/bbs")
public class BbsAdminController {

    @Autowired
    private BbsService bbsService;

    // 공지사항 작성 (관리자)
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<BbsDto> createBbs(
            @RequestParam Long adminId,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        if (type != BoardType.NORMAL) {
            throw new IllegalArgumentException("관리자는 NORMAL 게시판만 작성할 수 있습니다.");
        }

        BbsDto created = bbsService.createBbs(dto, null, adminId, files);
        return ResponseEntity.ok(created);
    }

    // QnA 답변 저장 (관리자)
    @PostMapping("/qna/{bbsId}/answer")
    public ResponseEntity<QandADto> saveQnaAnswer(
            @PathVariable Long bbsId,
            @RequestParam String adminId,
            @RequestBody QandADto dto) {

        QandADto saved = bbsService.saveQna(bbsId, dto, adminId);
        return ResponseEntity.ok(saved);
    }

    // QnA 답변 수정 (관리자)
    @PutMapping("/qna/{qnaId}")
    public ResponseEntity<QandADto> updateQnaAnswer(
            @PathVariable Long qnaId,
            @RequestBody QandADto dto) {

        QandADto updated = bbsService.updateQna(qnaId, dto);
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제 (단일)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBbs(
            @PathVariable Long id,
            @RequestParam Long adminId) {

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // 관리자 게시글 수정
    @PutMapping("/admin/{id}")
    public ResponseEntity<BbsDto> updateAdminBbs(
            @PathVariable Long id,
            @RequestParam Long adminId, // 관리자 ID
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(required = false) String deletedFileIds
    ) {
        List<Long> deleteIds = parseDeleteIds(deletedFileIds);

        BbsDto updated = bbsService.updateBbs(
                id, dto, adminId, files, deleteIds, true // isAdmin = true
        );
        return ResponseEntity.ok(updated);
    }

    // 관리자 게시글 첨부파일 수정 및 삭제
    @PutMapping("/file/{fileId}")
    public ResponseEntity<FileUpLoadDto> updateFile(
            @PathVariable Long fileId,
            @RequestPart("fileDto") FileUpLoadDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        FileUpLoadDto updatedFile = bbsService.updateFile(fileId, dto, file);
        return ResponseEntity.ok(updatedFile);
    }

    // QnA 답변 삭제 (관리자)
    @DeleteMapping("/qna/{qnaId}")
    public ResponseEntity<Void> deleteQnaAnswer(
            @PathVariable Long qnaId,
            @RequestParam Long adminId) {

        bbsService.deleteQna(qnaId, adminId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 리스트 조회
    @GetMapping("/bbslist")
    public ResponseEntity<Page<BbsDto>> getBbsList(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String bbscontent,
            @RequestParam(required = false) BoardType type,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<BbsDto> result = bbsService.searchPosts(searchType, bbstitle, bbscontent, type, pageable);
        return ResponseEntity.ok(result);
    }

    // 다중 삭제 (관리자)
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleBbs(
            @RequestParam Long adminId,
            @RequestBody List<Long> ids) {

        bbsService.deleteBbsMultiple(ids, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // 파일 ID 파싱
    private List<Long> parseDeleteIds(String deletedFileIds) {
        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(deletedFileIds, new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                throw new RuntimeException("삭제할 파일 ID 파싱 오류");
            }
        }
        return new ArrayList<>();
    }

}

