package com.project.board.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.service.BbsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/bbs")
public class MemberBbsController {

    @Autowired
    private BbsService bbsService;

    // ---------------- 게시글 작성 ----------------
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<BbsDto> createBbs(
            @RequestParam Long memberNum,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions,
            @RequestParam(value = "isRepresentative", required = false) List<String> isRepresentativeList
    ) {
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

        BbsDto created;
        if (type == BoardType.POTO) {
            created = bbsService.createPotoBbs(dto, memberNum, files, isRepresentativeList);
        } else {
            created = bbsService.createBbs(dto, memberNum, null, files, insertOptions, null);
        }

        return ResponseEntity.ok(created);
    }

    // ---------------- 게시글 수정 ----------------
    @PutMapping("/member/{id}")
    public ResponseEntity<BbsDto> updateMemberBbs(
            @PathVariable Long id,
            @RequestParam Long memberNum,
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

        BbsDto updated = bbsService.updateBbs(
                id, dto, memberNum, files, deleteIds, false, insertOptions
        );
        return ResponseEntity.ok(updated);
    }

    // ---------------- 게시글 삭제 ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBbs(
            @PathVariable Long id,
            @RequestParam Long memberNum) {

        bbsService.deleteBbs(id, memberNum, null);
        return ResponseEntity.noContent().build();
    }

    // ---------------- 게시글 단건 조회 ----------------
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBbs(@PathVariable Long id) {
        BbsDto dto = bbsService.getBbs(id); // BbsDto 고정
        ImageBbsDto repImg = bbsService.getRepresentativeImage(id); // 대표 이미지 별도 조회

        Map<String, Object> result = new HashMap<>();
        result.put("bbs", dto);
        result.put("representativeImage", repImg);

        return ResponseEntity.ok(result);
    }

    // ---------------- 게시글 목록 조회 ----------------
    @GetMapping("/bbslist")
    public ResponseEntity<Map<String, Object>> getBbsList(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String bbscontent,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) BoardType type,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<BbsDto> page = bbsService.searchPosts(searchType, bbstitle, bbscontent, memberName, type, pageable);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("content", page.getContent());
        pageMap.put("totalPages", page.getTotalPages());
        pageMap.put("number", page.getNumber());
        result.put("bbsList", pageMap);

        // 이미지 게시판(POTO)인 경우만 대표 이미지 Map 추가
        if (type == BoardType.POTO) {
            Map<String, ImageBbsDto> repImageMap = new HashMap<>();
            page.forEach(dto -> {
                ImageBbsDto repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
                if (repImg != null) repImageMap.put(dto.getBulletinNum().toString(), repImg);
            });
            result.put("representativeImages", repImageMap);
        }

        return ResponseEntity.ok(result);
    }


    // ---------------- 첨부파일 조회 ----------------
    @GetMapping("/{id}/files")
    public ResponseEntity<List<FileUpLoadDto>> getFilesByBbs(@PathVariable Long id) {
        List<FileUpLoadDto> files = bbsService.getFilesByBbs(id);
        return ResponseEntity.ok(files);
    }

    // ---------------- 첨부파일 업로드 ----------------
    @PostMapping("/{id}/files")
    public ResponseEntity<List<FileUpLoadDto>> uploadFiles(
            @PathVariable Long id,
            @RequestParam BoardType boardType,
            @RequestPart("files") List<MultipartFile> files) {
        List<FileUpLoadDto> uploaded = bbsService.saveFileList(id, files, boardType);
        return ResponseEntity.ok(uploaded);
    }

    // ---------------- 첨부파일 수정 ----------------
    @PutMapping("/files/{fileId}")
    public ResponseEntity<FileUpLoadDto> updateFile(
            @PathVariable Long fileId,
            @RequestPart(value = "file", required = false) MultipartFile newFile,
            @RequestPart("fileDto") FileUpLoadDto dto) {
        FileUpLoadDto updated = bbsService.updateFile(fileId, dto, newFile);
        return ResponseEntity.ok(updated);
    }

    // ---------------- 첨부파일 삭제 ----------------
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        bbsService.deleteFileById(fileId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- deletedFileIds 문자열 → List<Long> 변환 ----------------
    private List<Long> parseDeleteIds(String deletedFileIds) {
        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(deletedFileIds, new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                throw new RuntimeException("삭제할 파일 ID 파싱 오류", e);
            }
        }
        return new ArrayList<>();
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
        MediaType mediaType = "jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                ? MediaType.IMAGE_JPEG
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDto.getOriginalName() + "\"")
                .body(resource);
    }
}

