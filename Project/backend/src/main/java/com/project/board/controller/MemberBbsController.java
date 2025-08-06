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
@RequestMapping("/bbs")
public class MemberBbsController {

    @Autowired
    private BbsService bbsService;
    
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<BbsDto> createBbs(
            @RequestParam Long memberNum,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        

        if (type == BoardType.POTO) {
            // 이미지 게시판 별도 처리 (대표이미지 포함)
            BbsDto created = bbsService.createPotoBbs(dto, memberNum, files);
            return ResponseEntity.ok(created);
        } else {
            // QnA 등 일반 게시판 작성
            BbsDto created = bbsService.createBbs(dto, memberNum, null, files);
            return ResponseEntity.ok(created);
        }
    }

    // 게시글 수정 (본인만)
    @PutMapping("/{id}")//게시글번호
    public ResponseEntity<BbsDto> updateBbs(
            @PathVariable Long id,
            @RequestParam Long memberNum,
            @RequestPart("bbsDto") BbsDto dto) {

        BbsDto updated = bbsService.updateBbs(id, dto, memberNum);
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제 (본인만)
    @DeleteMapping("/{id}")	//게시글번호
    public ResponseEntity<Void> deleteBbs(
            @PathVariable Long id,
            @RequestParam Long memberNum) {

        bbsService.deleteBbs(id, memberNum, null);
        return ResponseEntity.noContent().build();
    }

    // 첨부파일 수정
    @PutMapping("/file/{fileId}")//수정데이터
    public ResponseEntity<FileUpLoadDto> updateFile(
            @PathVariable Long fileId,
            @RequestPart("fileDto") FileUpLoadDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        FileUpLoadDto updatedFile = bbsService.updateFile(fileId, dto, file);
        return ResponseEntity.ok(updatedFile);
    }

    // 이미지 수정 (대표 이미지 및 첨부 이미지)
    @PutMapping("/image/{bulletinNum}")
    public ResponseEntity<ImageBbsDto> updateImage(
            @PathVariable Long bulletinNum,
            @RequestPart("imageDto") ImageBbsDto dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        ImageBbsDto updatedImage = bbsService.updateImage(bulletinNum, dto, imageFile);
        return ResponseEntity.ok(updatedImage);
    }
    
    // 게시글 조회 (단건)
    @GetMapping("/{id}")//조건 확인
    public ResponseEntity<BbsDto> getBbs(@PathVariable Long id) {
        BbsDto dto = bbsService.getBbs(id);
        return ResponseEntity.ok(dto);
    }
}
