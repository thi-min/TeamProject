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
            BbsDto created = bbsService.createPotoBbs(dto, memberNum, files);
            return ResponseEntity.ok(created);
        } else {
            BbsDto created = bbsService.createBbs(dto, memberNum, null, files);
            return ResponseEntity.ok(created);
        }
    }


    // 게시글 수정 (본인만)
 // 회원 게시글 수정 (본인만 가능)
    @PutMapping("/member/{id}")
    public ResponseEntity<BbsDto> updateMemberBbs(
            @PathVariable Long id,
            @RequestParam Long memberNum, // 회원 번호
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(required = false) String deletedFileIds
    ) {
        List<Long> deleteIds = parseDeleteIds(deletedFileIds);

        BbsDto updated = bbsService.updateBbs(
                id, dto, memberNum, files, deleteIds, false // isAdmin = false
        );
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
