package com.project.board.controller;

import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.QandADto;
import com.project.board.service.BbsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/admin/bbs")
public class BbsAdminController {

    @Autowired
    private BbsService bbsService;

 // ---------------- 관리자 게시글 작성 (NORMAL 게시판) ----------------
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<BbsDto> createBbs(
            @RequestParam Long adminId,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions
    ) {

        if (type != BoardType.NORMAL) {
            throw new IllegalArgumentException("관리자는 NORMAL 게시판만 작성할 수 있습니다.");
        }

        // insertOptions와 파일 1:1 매칭 & jpg/jpeg 필터링
        if (files != null && insertOptions != null) {
            int size = Math.min(files.size(), insertOptions.size());
            for (int i = 0; i < size; i++) {
                MultipartFile file = files.get(i);
                String option = insertOptions.get(i);
                if ("insert".equals(option)) {
                    String contentType = file.getContentType();
                    if (!"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
                        insertOptions.set(i, "no-insert");
                    }
                }
            }
        }

        // isRepresentativeList는 NORMAL 게시판에서는 필요 없으므로 null로 전달
        BbsDto created = bbsService.createBbs(dto, null, adminId, files, insertOptions, null);

        return ResponseEntity.ok(created);
    }


    // ---------------- QnA 답변 저장 ----------------
    @PostMapping("/qna/{bbsId}/answer")
    public ResponseEntity<QandADto> saveQnaAnswer(
            @PathVariable Long bbsId,
            @RequestParam String adminId,
            @RequestBody QandADto dto) {

        QandADto saved = bbsService.saveQna(bbsId, dto, adminId);
        return ResponseEntity.ok(saved);
    }

    // ---------------- QnA 답변 수정 ----------------
    @PutMapping("/qna/{qnaId}")
    public ResponseEntity<QandADto> updateQnaAnswer(
            @PathVariable Long qnaId,
            @RequestBody QandADto dto) {

        QandADto updated = bbsService.updateQna(qnaId, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------------- 게시글 단건 삭제 ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBbs(
            @PathVariable Long id,
            @RequestParam Long adminId) {

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- 관리자 게시글 수정 ----------------
    @PutMapping("/admin/{id}")
    public ResponseEntity<BbsDto> updateAdminBbs(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(required = false) String deletedFileIds,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions
    ) {

        List<Long> deleteIds = parseDeleteIds(deletedFileIds);

        // insertOptions와 파일 1:1 매칭 & jpg/jpeg 필터링
        if (files != null && insertOptions != null) {
            int size = Math.min(files.size(), insertOptions.size());
            for (int i = 0; i < size; i++) {
                MultipartFile file = files.get(i);
                String option = insertOptions.get(i);
                if ("insert".equals(option)) {
                    String contentType = file.getContentType();
                    if (!"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
                        insertOptions.set(i, "no-insert");
                    }
                }
            }
        }

        BbsDto updated = bbsService.updateBbs(
                id, dto, adminId, files, deleteIds, true, insertOptions
        );
        return ResponseEntity.ok(updated);
    }

    // ---------------- deletedFileIds 문자열 → List<Long> 변환 ----------------
    private List<Long> parseDeleteIds(String deletedFileIds) {
        if (deletedFileIds == null || deletedFileIds.isEmpty()) return new ArrayList<>();
        String[] parts = deletedFileIds.split(",");
        List<Long> ids = new ArrayList<>();
        for (String part : parts) {
            try {
                ids.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return ids;
    }
    
 // ---------------- 첨부파일 다운로드 ----------------
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        // Service에서 단일 파일 조회
        FileUpLoadDto fileDto = bbsService.getFileById(fileId);

        // 파일 경로 준비
        Path path = Paths.get(fileDto.getPath(), fileDto.getSavedName());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // jpg/jpeg 처리, 나머지는 일반 다운로드
        String ext = fileDto.getExtension();
        MediaType mediaType;
        if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
            mediaType = MediaType.IMAGE_JPEG;
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDto.getOriginalName() + "\"")
                .body(resource);
    }
}

