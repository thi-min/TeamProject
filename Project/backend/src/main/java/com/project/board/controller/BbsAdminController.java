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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/admin/bbs")
public class BbsAdminController {

    @Autowired
    private BbsService bbsService;

 // ---------------- 관리자용 공지사항 게시글 조회 (최신순) ----------------
    @GetMapping("/notices")
    public ResponseEntity<Map<String, Object>> getNoticeBbsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String bbscontent
    ) {
        // type은 무조건 NORMAL
        BoardType type = BoardType.NORMAL;

        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }

    
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

 // ---------------- 다중 삭제 ----------------
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleBbs(
            @RequestParam List<Long> ids,
            @RequestParam Long adminId) {

        bbsService.deleteBbsMultiple(ids, null, adminId); // requesterMemberNum은 null, adminId 전달
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

        BbsDto updated = bbsService.updateBbs(id, dto, adminId, files, deleteIds, true, insertOptions);
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
        FileUpLoadDto fileDto = bbsService.getFileById(fileId);
        Path path = Paths.get(fileDto.getPath(), fileDto.getSavedName());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String ext = fileDto.getExtension();
        MediaType mediaType = ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext))
                ? MediaType.IMAGE_JPEG
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDto.getOriginalName() + "\"")
                .body(resource);
    }

    // ---------------- 관리자용 FAQ 게시글 조회 (최신순) ----------------

    @GetMapping("/bbslist")
    public ResponseEntity<Map<String, Object>> getFaqBbsList(
            @RequestParam BoardType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String bbscontent
    ) {
        if (type != BoardType.FAQ) {
            throw new IllegalArgumentException("관리자 FAQ 조회는 FAQ 타입만 가능합니다.");
        }

        // 기존 getBbsList 호출
        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }
    
 // ---------------- 관리자용 이미지 게시글 조회 (최신순) ----------------
    @GetMapping("/poto")
    public ResponseEntity<Map<String, Object>> getPotoBbsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String bbscontent
    ) {
        // type은 무조건 POTO
        BoardType type = BoardType.POTO;

        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }

 // 관리자 이미지 게시글 단건 조회
    @GetMapping("/poto/{id}")
    public ResponseEntity<BbsDto> getPotoBbsDetail(@PathVariable Long id) {
        BbsDto dto = bbsService.getBbs(id);
        return ResponseEntity.ok(dto);
    }

}

