package com.project.board.controller;

import com.project.board.BoardType;
import com.project.board.dto.*;
import com.project.board.service.BbsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 게시글 삭제 (관리자는 회원글 및 관리자글 모두 삭제 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBbs(
            @PathVariable Long id,
            @RequestParam Long adminId) {

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // 관리자 본인 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BbsDto> updateBbs(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody BbsDto dto) {

        BbsDto updated = bbsService.updateBbs(id, dto, null);
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

}
