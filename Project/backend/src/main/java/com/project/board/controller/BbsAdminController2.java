//package com.project.board.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.board.BoardType;
//import com.project.board.dto.BbsDto;
//import com.project.board.dto.FileUpLoadDto;
//import com.project.board.dto.ImageBbsDto;
//import com.project.board.dto.QandADto;
//import com.project.board.entity.BbsEntity;
//import com.project.board.exception.BbsException;
//import com.project.board.repository.BbsRepository;
//import com.project.board.service.BbsService;
//import com.project.common.jwt.JwtTokenProvider;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/admin/bbs")
//public class BbsAdminController2 {
//
//    @Autowired
//    private BbsService bbsService;
//    
//    @Autowired
//    private BbsRepository bbsRepository;
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//    
//    
//    private final String BACKEND_URL = "http://127.0.0.1:8090";
//
//    // ---------------- 관리자용 공지사항 게시글 조회 (최신순) ----------------
//    @GetMapping("/notices")
//    public ResponseEntity<Map<String, Object>> getNoticeBbsList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String bbstitle,
//            @RequestParam(required = false) String memberName,
//            @RequestParam(required = false) String bbscontent
//    ) {
//        BoardType type = BoardType.NORMAL;
//        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
//        return ResponseEntity.ok(result);
//    }
//
//    // ---------------- 관리자 게시글 작성 (NORMAL 게시판) ----------------
//    @PostMapping("/bbslist/bbsadd")
//    public ResponseEntity<BbsDto> createBbs(
//            @RequestHeader("Authorization") String authorizationHeader,
//            @RequestParam BoardType type,
//            @RequestPart("bbsDto") BbsDto dto,
//            @RequestPart(value = "files", required = false) List<MultipartFile> files,
//            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions
//    ) {
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
//        if (!"ADMIN".equals(role))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        if (type != BoardType.NORMAL)
//            throw new IllegalArgumentException("관리자는 NORMAL 게시판만 작성할 수 있습니다.");
//
//        dto.setBulletinType(type);
//
//        // insertOptions 검증: 본문 삽입은 jpg/jpeg만 가능, 첨부 가능 파일만 등록
//        if (files != null && insertOptions != null) {
//            int size = Math.min(files.size(), insertOptions.size());
//            for (int i = 0; i < size; i++) {
//                MultipartFile file = files.get(i);
//                String option = insertOptions.get(i);
//                String contentType = file.getContentType();
//                String filename = file.getOriginalFilename();
//                String ext = filename != null && filename.contains(".") ?
//                        filename.substring(filename.lastIndexOf(".") + 1).toLowerCase() : "";
//
//                // 첨부 가능한 파일 체크
//                if (!Arrays.asList("jpg", "jpeg", "pdf", "ppt", "pptx", "doc", "docx").contains(ext)) {
//                    insertOptions.set(i, "no-insert");
//                    continue;
//                }
//
//                // 본문 삽입 가능 파일 체크
//                if ("insert".equals(option)) {
//                    if (!ext.equals("jpg") && !ext.equals("jpeg")) {
//                        insertOptions.set(i, "no-insert");
//                    }
//                }
//            }
//        }
//
//        BbsDto created = bbsService.createBbs(dto, null, adminId, files, insertOptions, null);
//        return ResponseEntity.ok(created);
//    }
//
//    // ---------------- Normal 게시글 단건 조회 ----------------
//    @GetMapping("/normal/{id}")
//    public ResponseEntity<BbsDto> getNormalBbsDetail(
//            @PathVariable Long id,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        if (!"ADMIN".equals(role))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//
//        BbsDto dto = bbsService.getBbs(id);
//        if (dto == null)
//            return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(dto);
//    }
//
//    // ---------------- Normal 게시글 삭제 ----------------
//    @DeleteMapping("/normal/{id}")
//    public ResponseEntity<Void> deleteNormalBbs(
//            @PathVariable Long id,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
//        if (!"ADMIN".equals(role))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//
//        BbsDto dto = bbsService.getBbs(id);
//        if (dto == null || dto.getBulletinType() != BoardType.NORMAL)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//
//        bbsService.deleteBbs(id, null, adminId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ---------------- Normal 게시글 첨부파일 조회 ----------------
//    @GetMapping("/normal/{id}/files")
//    public ResponseEntity<List<Map<String, Object>>> getNormalFiles(@PathVariable Long id) {
//        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
//        List<Map<String, Object>> fileMapList = new ArrayList<>();
//
//        for (FileUpLoadDto f : filesList) {
//            Map<String, Object> fileMap = new HashMap<>();
//            fileMap.put("fileNum", f.getFileNum());
//            fileMap.put("originalName", f.getOriginalName());
//            fileMap.put("savedName", f.getSavedName());
//            fileMap.put("path", f.getPath());
//            fileMap.put("size", f.getSize());
//            fileMap.put("extension", f.getExtension());
//            fileMap.put("fileUrl", "http://127.0.0.1:8090/admin/bbs/files/" +
//                    f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//
//        return ResponseEntity.ok(fileMapList);
//    }
//
//    // ---------------- 답변 작성 ----------------
//    @PostMapping("/qna/{bbsId}/answer")
//    public ResponseEntity<QandADto> saveQnaAnswer(
//            @PathVariable Long bbsId,
//            @RequestBody QandADto dto,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new QandADto());
//        }
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
//        if (!"ADMIN".equals(role)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new QandADto());
//        }
//
//        QandADto savedDto = bbsService.saveQna(bbsId, dto, adminId);
//        return ResponseEntity.ok(savedDto);
//    }
//
//    // ---------------- QnA 답변 수정 ----------------
//    @PutMapping("/qna/{qnaId}")
//    public ResponseEntity<QandADto> updateQnaAnswer(
//            @PathVariable Long qnaId,
//            @RequestBody QandADto dto) {
//        QandADto updated = bbsService.updateQna(qnaId, dto);
//        return ResponseEntity.ok(updated);
//    }
//
//    // ---------------- 게시글 단건 삭제 ----------------
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBbs(
//            @PathVariable Long id,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
//
//        if (!"ADMIN".equals(role)) {
//            throw new BbsException("삭제 권한이 없습니다.");
//        }
//
//        bbsService.deleteBbs(id, null, adminId);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ---------------- 다중 삭제 ----------------
//    @DeleteMapping("/delete-multiple")
//    public ResponseEntity<Void> deleteMultipleBbs(
//            @RequestParam List<Long> ids,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String role = jwtTokenProvider.getRoleFromToken(token);
//        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
//        if (!"ADMIN".equals(role))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//
//        bbsService.deleteBbsMultiple(ids, null, adminId);
//        return ResponseEntity.noContent().build();
//    }
//
// // ---------------- 관리자 일반 게시글 수정 ----------------
//    @PutMapping(value = "/normal/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Transactional
//    public ResponseEntity<Map<String, Object>> updateAdminNormalBbs(
//            @PathVariable Long id,
//            @RequestHeader("Authorization") String authorizationHeader,
//            @RequestParam("bbsTitle") String bbsTitle,
//            @RequestParam("bbsContent") String bbsContent,
//            @RequestParam(value = "files", required = false) List<MultipartFile> files,
//            @RequestParam(value = "deletedFileIds", required = false) String deletedFileIds,
//            @RequestParam(value = "insertOptions", required = false) String insertOptionsCsv
//    ) {
//        try {
//            String token = authorizationHeader.replace("Bearer ", "");
//            if (!jwtTokenProvider.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            if (!"ADMIN".equals(jwtTokenProvider.getRoleFromToken(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//
//            Map<String, Object> response = new HashMap<>();
//
//            List<Long> deletedIdsList = (deletedFileIds != null && !deletedFileIds.isBlank())
//                    ? parseDeleteIds(deletedFileIds)
//                    : new ArrayList<>();
//            deletedIdsList.forEach(bbsService::deleteFileById);
//
//            List<String> insertOptions = (insertOptionsCsv != null && !insertOptionsCsv.isBlank())
//                    ? Arrays.asList(insertOptionsCsv.split(","))
//                    : new ArrayList<>();
//
//            BbsDto dto = new BbsDto();
//            dto.setBbsTitle(bbsTitle);
//            dto.setBbsContent(bbsContent);
//
//            BbsDto updated = bbsService.updateBbs(
//                    id,
//                    dto,
//                    null, // memberNum 생략
//                    null, // adminId 생략
//                    files,
//                    deletedIdsList,
//                    true,
//                    insertOptions
//            );
//            response.put("bbs", updated);
//
//            List<Map<String, Object>> fileMapList = new ArrayList<>();
//            if (files != null) {
//                for (MultipartFile f : files) {
//                    Map<String, Object> fileMap = new HashMap<>();
//                    fileMap.put("fileName", f.getOriginalFilename());
//                    fileMap.put("size", f.getSize());
//                    fileMapList.add(fileMap);
//                }
//            }
//            response.put("files", fileMapList);
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
//    // ---------------- 본문 삽입 처리 ----------------
//    private String insertFilesToContent(String originalContent, List<FileUpLoadDto> files, List<String> insertOptions) {
//        StringBuilder content = new StringBuilder(originalContent == null ? "" : originalContent);
//        List<String> imageExt = List.of("jpg", "jpeg", "png"); // 본문 삽입 허용 이미지 확장자
//
//        for (int i = 0; i < files.size(); i++) {
//            FileUpLoadDto file = files.get(i);
//            String option = (insertOptions != null && insertOptions.size() > i) ? insertOptions.get(i) : "no-insert";
//            String ext = file.getExtension().toLowerCase();
//            String url = "/uploads/" + file.getSavedName();
//
//            if ("insert".equals(option) && imageExt.contains(ext)) {
//                content.append("\n<img src=\"")
//                       .append(url)
//                       .append("\" alt=\"")
//                       .append(file.getOriginalName())
//                       .append("\" style='max-width:600px;' />");
//            }
//        }
//
//        return content.toString();
//    }
//
//
//    private List<Long> parseDeleteIds(String deletedFileIds) {
//        if (deletedFileIds == null || deletedFileIds.isEmpty()) return new ArrayList<>();
//        String[] parts = deletedFileIds.split(",");
//        List<Long> ids = new ArrayList<>();
//        for (String part : parts) {
//            try {
//                ids.add(Long.parseLong(part.trim()));
//            } catch (NumberFormatException ignored) {}
//        }
//        return ids;
//    }
//
//    // ---------------- 관리자용 FAQ 게시글 조회 ----------------
//    @GetMapping("/bbslist")
//    public ResponseEntity<Map<String, Object>> getFaqBbsList(
//            @RequestParam BoardType type,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String bbstitle,
//            @RequestParam(required = false) String memberName,
//            @RequestParam(required = false) String bbscontent
//    ) {
//        if (type != BoardType.FAQ) {
//            throw new IllegalArgumentException("관리자 FAQ 조회는 FAQ 타입만 가능합니다.");
//        }
//        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
//        return ResponseEntity.ok(result);
//    }
//
//    // ---------------- 관리자 QnA 게시글 단건 조회 ----------------
//    @GetMapping("/qna/{id}")
//    public ResponseEntity<Map<String, Object>> getQnaBbsDetail(@PathVariable Long id) {
//        BbsDto bbsDto = bbsService.getBbs(id);
//        QandADto qnaDto = bbsService.getQna(id);
//
//        if (bbsDto == null) return ResponseEntity.notFound().build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("bbs", bbsDto);
//        response.put("answer", qnaDto != null ? qnaDto.getAnswer() : null);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ---------------- 관리자용 이미지 게시글 조회 ----------------
//    @GetMapping("/poto")
//    public ResponseEntity<Map<String, Object>> getPotoBbsList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "12") int size,
//            @RequestParam(required = false) String bbstitle,
//            @RequestParam(required = false) String memberName,
//            @RequestParam(required = false) String bbscontent
//    ) {
//        BoardType type = BoardType.POTO;
//        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
//
//        List<BbsDto> content = (List<BbsDto>) result.get("list");
//        if (content == null) content = new ArrayList<>();
//
//        Map<String, Object> repImages = new HashMap<>();
//        for (BbsDto dto : content) {
//            var repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
//            Map<String, Object> imgMap = new HashMap<>();
//            if (repImg != null) {
//                imgMap.put("bulletinNum", dto.getBulletinNum());
//                imgMap.put("thumbnailPath", repImg.getThumbnailPath());
//                imgMap.put("imagePath", repImg.getImagePath() != null ? "http://127.0.0.1:8090" + repImg.getImagePath() : "");
//            }
//            repImages.put(dto.getBulletinNum().toString(), imgMap);
//        }
//
//        result.put("representativeImages", repImages);
//        return ResponseEntity.ok(result);
//    }
//
//    // ---------------- 관리자 이미지 게시글 단건 조회 ----------------
//    @GetMapping("/poto/{id}")
//    public ResponseEntity<Map<String, Object>> getPotoBbsDetail(@PathVariable Long id) {
//        BbsDto dto = bbsService.getBbs(id);
//
//        // 대표 이미지
//        ImageBbsDto repImg = bbsService.getRepresentativeImage(id);
//        Map<String, Object> repImgMap = null;
//        if (repImg != null) {
//            repImgMap = new HashMap<>();
//            repImgMap.put("bulletinNum", repImg.getBulletinNum());
//            repImgMap.put("thumbnailPath", repImg.getThumbnailPath());
//            repImgMap.put("imagePath", repImg.getImagePath() != null ? BACKEND_URL + repImg.getImagePath() : null);
//        }
//
//        // 첨부파일
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
//            fileMap.put("fileUrl", BACKEND_URL + "/admin/bbs/files/" + f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//
//        // 결과
//        Map<String, Object> result = new HashMap<>();
//        result.put("bbs", dto);
//        result.put("representativeImage", repImgMap);
//        result.put("files", fileMapList);
//
//        return ResponseEntity.ok(result);
//    }
//
//
//    // ---------------- 관리자 게시글 첨부파일 조회 ----------------
//    @GetMapping("/{id}/files")
//    public ResponseEntity<List<Map<String, Object>>> getFilesByBbs(@PathVariable Long id) {
//        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
//        List<Map<String, Object>> fileMapList = new ArrayList<>();
//
//        for (FileUpLoadDto f : filesList) {
//            Map<String, Object> fileMap = new HashMap<>();
//            fileMap.put("fileNum", f.getFileNum());
//            fileMap.put("originalName", f.getOriginalName());
//            fileMap.put("savedName", f.getSavedName());
//            fileMap.put("path", f.getPath());
//            fileMap.put("size", f.getSize());
//            fileMap.put("extension", f.getExtension());
//            fileMap.put("fileUrl", "http://127.0.0.1:8090/admin/bbs/files/" + f.getFileNum() + "/download");
//            fileMapList.add(fileMap);
//        }
//
//        return ResponseEntity.ok(fileMapList);
//    }
//
//    // ---------------- 첨부파일 다운로드 ----------------
//    @GetMapping("/files/{fileId}/download")
//    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
//        FileUpLoadDto fileDto = bbsService.getFileById(fileId);
//        if (fileDto == null) return ResponseEntity.notFound().build();
//
//        Path path = Paths.get(fileDto.getPath(), fileDto.getSavedName());
//        Resource resource = new FileSystemResource(path);
//        if (!resource.exists()) return ResponseEntity.notFound().build();
//
//        MediaType mediaType;
//        String ext = fileDto.getExtension().toLowerCase();
//        switch (ext) {
//            case "jpeg":
//            case "jpg":
//                mediaType = MediaType.IMAGE_JPEG;
//                break;
//            case "pdf":
//                mediaType = MediaType.APPLICATION_PDF;
//                break;
//            case "ppt":
//            case "pptx":
//            case "doc":
//            case "docx":
//                mediaType = MediaType.APPLICATION_OCTET_STREAM;
//                break;
//            default:
//                mediaType = MediaType.APPLICATION_OCTET_STREAM;
//        }
//
//        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok().contentType(mediaType);
//        if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
//            responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
//                    "attachment; filename=\"" + fileDto.getOriginalName() + "\"");
//        }
//
//        return responseBuilder.body(resource);
//    }
//}
