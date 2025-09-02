//package com.project.board.controller;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.board.BoardType;
//import com.project.board.dto.BbsDto;
//import com.project.board.dto.BbsSimpleResponseDto;
//import com.project.board.dto.FileUpLoadDto;
//import com.project.board.dto.ImageBbsDto;
//import com.project.board.entity.QandAEntity;
//import com.project.board.exception.BbsException;
//import com.project.board.repository.QandARepository;
//import com.project.board.service.BbsService;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/bbs")
//@RequiredArgsConstructor 
//public class MemberBbsController2 {
//
//@Autowired  
//    private BbsService bbsService;
//
//	private final QandARepository qandARepository;
//	
//    private final String BACKEND_URL = "http://127.0.0.1:8090";
//
//    // ---------------- 게시글 작성 ----------------
//    @PostMapping("/bbslist/bbsadd")
//    public ResponseEntity<Map<String, Object>> createBbs(
//            @RequestParam Long memberNum,
//            @RequestParam BoardType type,
//            @RequestPart("bbsDto") BbsDto dto,
//            @RequestPart(value = "files", required = false) List<MultipartFile> files,
//            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions,
//            @RequestParam(value = "isRepresentative", required = false) List<String> isRepresentativeList
//    ) {
//        // 이미지 파일만 insert 가능하게 필터링
//        if (files != null && insertOptions != null) {
//            int size = Math.min(files.size(), insertOptions.size());
//            for (int i = 0; i < size; i++) {
//                MultipartFile file = files.get(i);
//                String option = insertOptions.get(i);
//                if ("insert".equals(option)) {
//                    String contentType = file.getContentType();
//                    if (!"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
//                        insertOptions.set(i, "no-insert");
//                    }
//                }
//            }
//        }
//
//        BbsDto created;
//        if (type == BoardType.POTO) {
//            created = bbsService.createPotoBbs(dto, memberNum, files, isRepresentativeList);
//        } else {
//            created = bbsService.createBbs(dto, memberNum, null, files, insertOptions, null);
//        }
//
//        // ---------------- Map 구조로 변환 ----------------
//        Map<String, Object> response = new HashMap<>();
//        response.put("bbs", created);
//
//        // ✅ 대표 이미지(POTO 타입만) 조회
//        ImageBbsDto repImg = null;
//        Map<String, Object> repImgMap = null;
//        if (type == BoardType.POTO) {
//            repImg = bbsService.getRepresentativeImage(created.getBulletinNum());
//            if (repImg != null) {
//                repImgMap = new HashMap<>();
//                repImgMap.put("bulletinNum", repImg.getBulletinNum());
//                repImgMap.put("thumbnailPath", repImg.getThumbnailPath());
//                repImgMap.put("imagePath", repImg.getImagePath() != null ? BACKEND_URL + repImg.getImagePath() : null);
//            }
//        }
//        response.put("representativeImage", repImgMap);
//
//        // 첨부파일 조회 (모든 게시판)
//        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(created.getBulletinNum());
//        List<Map<String, Object>> fileMapList = new ArrayList<>();
//        for (FileUpLoadDto f : filesList) {
//            Map<String, Object> fileMap = new HashMap<>();
//            fileMap.put("fileNum", f.getFileNum());
//            fileMap.put("originalName", f.getOriginalName());
//            fileMap.put("savedName", f.getSavedName());
//            fileMap.put("path", f.getPath());
//            fileMap.put("size", f.getSize());
//            fileMap.put("extension", f.getExtension());
//            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//        response.put("files", fileMapList);
//
//        return ResponseEntity.ok(response);
//    }
//
//    
//    
// // ---------------- 게시글 수정 ----------------
//    @PutMapping("/member/{id}")
//    @Transactional
//    public ResponseEntity<Map<String, Object>> updateBbsByType(
//            @PathVariable Long id,
//            @RequestPart("memberNum") Long memberNum,
//            @RequestPart("bbsDto") BbsDto dto,
//            @RequestPart(value = "files", required = false) List<MultipartFile> files,
//            @RequestPart(value = "deletedFileIds", required = false) String deletedFileIds, // 이미지 게시판은 String, 일반은 List<Long> 처리
//            @RequestPart(value = "overwriteFileIds", required = false) String overwriteFileIds,
//            @RequestPart(value = "insertOptions", required = false) List<String> insertOptions,
//            @RequestPart(value = "isRepresentativeList", required = false) String isRepresentativeList,
//            @RequestParam(value = "isAdmin", defaultValue = "false") boolean isAdmin,
//            @RequestPart(value = "adminId", required = false) String adminId
//    ) {
//        try {
//            Map<String, Object> response = new HashMap<>();
//
//            if (dto.getBulletinType() == BoardType.POTO) {
//                // ==================== 이미지 게시판 처리 ====================
//                List<Long> deleteIds = (deletedFileIds != null && !deletedFileIds.isBlank())
//                        ? parseDeleteIds(deletedFileIds)
//                        : new ArrayList<>();
//
//                List<Long> overwriteIds = (overwriteFileIds != null && !overwriteFileIds.isBlank())
//                        ? parseDeleteIds(overwriteFileIds)
//                        : new ArrayList<>();
//
//                if (!overwriteIds.isEmpty()) {
//                    deleteIds.addAll(overwriteIds); // 덮어쓰기 파일은 삭제 처리
//                }
//
//                List<MultipartFile> newFilesList = (files != null)
//                        ? files.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList())
//                        : Collections.emptyList();
//
//                // 대표 이미지 id 처리
//                List<Long> representativeIds = new ArrayList<>();
//                if (isRepresentativeList != null && !isRepresentativeList.isBlank()) {
//                    try {
//                        representativeIds.add(Long.valueOf(isRepresentativeList));
//                    } catch (NumberFormatException e) {
//                        throw new BbsException("대표 이미지 ID가 유효하지 않습니다: " + isRepresentativeList);
//                    }
//                }
//                if (representativeIds.isEmpty()) {
//                    throw new BbsException("대표 이미지는 반드시 1장 선택해야 합니다.");
//                }
//
//                // Service 호출
//                BbsDto updated = bbsService.updatePotoBbs(
//                        id,
//                        dto,
//                        newFilesList,
//                        representativeIds,
//                        deleteIds,
//                        overwriteIds,
//                        memberNum
//                );
//
//                response.put("bbs", updated);
//
//                // 대표 이미지
//                ImageBbsDto repImg = bbsService.getRepresentativeImage(updated.getBulletinNum());
//                Map<String, Object> repImgMap = null;
//                if (repImg != null) {
//                    repImgMap = new HashMap<>();
//                    repImgMap.put("bulletinNum", repImg.getBulletinNum());
//                    repImgMap.put("thumbnailPath", repImg.getThumbnailPath());
//                    repImgMap.put("imagePath", repImg.getImagePath() != null ? BACKEND_URL + repImg.getImagePath() : null);
//                }
//                response.put("representativeImage", repImgMap);
//
//                // 첨부파일 리스트
//                List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(updated.getBulletinNum());
//                List<Map<String, Object>> fileMapList = new ArrayList<>();
//                for (FileUpLoadDto f : filesList) {
//                    Map<String, Object> fileMap = new HashMap<>();
//                    fileMap.put("fileNum", f.getFileNum());
//                    fileMap.put("originalName", f.getOriginalName());
//                    fileMap.put("savedName", f.getSavedName());
//                    fileMap.put("path", f.getPath());
//                    fileMap.put("size", f.getSize());
//                    fileMap.put("extension", f.getExtension());
//                    fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
//                    fileMapList.add(fileMap);
//                }
//                response.put("files", fileMapList);
//
//            } else {
//                // ==================== 일반 게시판 처리 ====================
//                List<Long> deletedIdsList = deletedFileIds != null
//                        ? parseDeleteIds(deletedFileIds) // String -> List<Long> 또는 List<Long> 그대로 전달
//                        : new ArrayList<>();
//
//                BbsDto updated = bbsService.updateBbs(
//                        id,
//                        dto,
//                        memberNum,
//                        adminId,
//                        files,
//                        deletedIdsList,
//                        isAdmin,
//                        insertOptions
//                );
//
//                response.put("bbs", updated);
//
//                // 첨부파일 리스트
//                List<Map<String, Object>> fileMapList = new ArrayList<>();
//                if (files != null) {
//                    for (MultipartFile f : files) {
//                        Map<String, Object> fileMap = new HashMap<>();
//                        fileMap.put("fileName", f.getOriginalFilename());
//                        fileMap.put("size", f.getSize());
//                        fileMapList.add(fileMap);
//                    }
//                }
//                response.put("files", fileMapList);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (BbsException e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
//        } catch (Exception e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("error", "서버 오류: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(error);
//        }
//    }
//
//
//
//
//
//    // ---------------- 게시글 삭제 ----------------
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBbs(
//            @PathVariable Long id,
//            @RequestParam Long memberNum) {
//        bbsService.deleteBbs(id, memberNum, null);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ---------------- 게시글 단건 조회 ----------------
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getBbs(@PathVariable Long id) {
//        // 1. 게시글 정보 조회
//        BbsDto dto = bbsService.getBbs(id);
//
//        // 2. 대표 이미지(POTO 타입만) 조회
//        ImageBbsDto repImg = null;
//        Map<String, Object> repImgMap = null;
//        if (dto.getBulletinType() == BoardType.POTO) {
//            repImg = bbsService.getRepresentativeImage(id);
//            if (repImg != null) {
//                repImgMap = new HashMap<>();
//                repImgMap.put("bulletinNum", repImg.getBulletinNum());
//                repImgMap.put("thumbnailPath", repImg.getThumbnailPath());
//                repImgMap.put("imagePath", repImg.getImagePath() != null ? BACKEND_URL + repImg.getImagePath() : null);
//            }
//        }
//
//        // 3. 첨부파일 조회 (모든 게시판)
//        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
//        List<Map<String, Object>> fileMapList = new ArrayList<>();
//        for (FileUpLoadDto f : filesList) {
//            Map<String, Object> fileMap = new HashMap<>();
//            fileMap.put("fileNum", f.getFileNum());
//            fileMap.put("originalName", f.getOriginalName());
//            fileMap.put("savedName", f.getSavedName());
//            fileMap.put("path", f.getPath());
//            fileMap.put("size", f.getSize());
//            fileMap.put("extension", f.getExtension());
//            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//
//     // 4. FAQ/Answer 처리
//        String answer = null;
//        if (dto.getBulletinType() == BoardType.FAQ) {
//            Optional<QandAEntity> qaEntity = qandARepository.findByBbsBulletinNum(id);
//            if (qaEntity.isPresent()) {
//                answer = qaEntity.get().getAnswer();
//            }
//        }
//        
//        // 5. 결과 맵 구성
//        Map<String, Object> result = new HashMap<>();
//        result.put("bbs", dto);
//        result.put("representativeImage", repImgMap);
//        result.put("files", fileMapList);
//        
//        if (dto.getBulletinType() == BoardType.FAQ) {  
//            result.put("answer", answer);  
//        }
//        return ResponseEntity.ok(result);
//    }
//
//
// // ---------------- 게시글 목록 조회 ----------------
//    @GetMapping("/bbslist")
//    public ResponseEntity<Map<String, Object>> getBbsList(
//            @RequestParam(required = false) String searchType,
//            @RequestParam(required = false) String bbstitle,
//            @RequestParam(required = false) String bbscontent,
//            @RequestParam(required = false) String memberName,
//            @RequestParam(required = false) BoardType type,
//            @PageableDefault(size = 10) Pageable pageable) {
//
//        Page<BbsDto> page = bbsService.searchPosts(searchType, bbstitle, bbscontent, memberName, type, pageable);
//
//        Map<String, Object> result = new HashMap<>();
//        Map<String, Object> pageMap = new HashMap<>();
//        pageMap.put("content", page.getContent());
//        pageMap.put("totalPages", page.getTotalPages());
//        pageMap.put("number", page.getNumber());
//        result.put("bbsList", pageMap);
//
//        // 대표 이미지 매핑
//        if (type == BoardType.POTO) {
//            Map<String, Object> repImageMap = new HashMap<>();
//            page.getContent().forEach(dto -> {
//                ImageBbsDto repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
//                Map<String, Object> repMap = new HashMap<>();
//                repMap.put("bulletinNum", dto.getBulletinNum());
//                repMap.put("thumbnailPath", repImg != null ? repImg.getThumbnailPath() : "");
//                repMap.put("imagePath", repImg != null ? repImg.getImagePath() : "");
//                repImageMap.put(dto.getBulletinNum().toString(), repMap);
//            });
//            result.put("representativeImages", repImageMap);
//        }
//
//        return ResponseEntity.ok(result);
//    }
//
//
//
//    // ---------------- 첨부파일 조회 ----------------
//    @GetMapping("/{id}/files")
//    public ResponseEntity<List<Map<String, Object>>> getFilesByBbs(@PathVariable Long id) {
//        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
//        List<Map<String, Object>> fileMapList = new ArrayList<>();
//        for (FileUpLoadDto f : filesList) {
//            Map<String, Object> fileMap = new HashMap<>();
//            fileMap.put("fileNum", f.getFileNum());
//            fileMap.put("originalName", f.getOriginalName());
//            fileMap.put("savedName", f.getSavedName());
//            fileMap.put("path", f.getPath());
//            fileMap.put("size", f.getSize());
//            fileMap.put("extension", f.getExtension());
//            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//        return ResponseEntity.ok(fileMapList);
//    }
//
//    // ---------------- deletedFileIds 문자열 → List<Long> 변환 ----------------
//    private List<Long> parseDeleteIds(String deletedFileIds) {
//        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
//            ObjectMapper mapper = new ObjectMapper();
//            try {
//                return mapper.readValue(deletedFileIds, new TypeReference<List<Long>>() {});
//            } catch (Exception e) {
//                throw new RuntimeException("삭제할 파일 ID 파싱 오류", e);
//            }
//        }
//        return new ArrayList<>();
//    }
//
//    // ---------------- 첨부파일 다운로드 ----------------
//    @GetMapping("/files/{fileId}/download")
//    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId,
//                                                 @RequestParam(required = false) String boardType) {
//        FileUpLoadDto fileDto = bbsService.getFileById(fileId);
//        Path path = Paths.get(fileDto.getPath(), fileDto.getSavedName());
//        Resource resource = new FileSystemResource(path);
//
//        if (!resource.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // 이미지 게시판(boardType=image)에서만 jpg, jpeg 허용
//        if ("image".equals(boardType)) {
//            String ext = fileDto.getExtension().toLowerCase();
//            if (!ext.equals("jpg") && !ext.equals("jpeg")) {
//                return ResponseEntity.badRequest().build();
//            }
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + fileDto.getOriginalName() + "\"")
//                .body(resource);
//    }
//
//    //게시판 공지사항 조회 최신5개 안형주 추가 09.01
//    @GetMapping("/latest")
//    public List<BbsSimpleResponseDto> getLatestNormalPosts() {
//        return bbsService.getLatestNormalPosts();
//    }
//}
