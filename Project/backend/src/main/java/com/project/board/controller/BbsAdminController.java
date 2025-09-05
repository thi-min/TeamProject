package com.project.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.dto.QandADto;
import com.project.board.entity.BbsEntity;
import com.project.board.exception.BbsException;
import com.project.board.repository.BbsRepository;
import com.project.board.service.BbsService;
import com.project.common.jwt.JwtTokenProvider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // ✅ 추가: 물리 경로 주입용
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/bbs")
public class BbsAdminController {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private BbsRepository bbsRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ⚠️ 프론트는 /DATA/... 로 직접 접근하므로 이미지/파일 미리보기에는 BACKEND_URL을 붙이지 않음
    private final String BACKEND_URL = "http://127.0.0.1:8090";

    // =========================
    // 📌 application.properties 값 주입 (물리 저장소 경로)
    //    downloadFile 에서 DB의 /DATA/... 를 물리경로로 매핑하는 데 사용
    // =========================
    @Value("${file.upload-imgbbs}")
    private String imgBbsUploadDir;    // ../frontend/public/DATA/bbs/imgBbs

    @Value("${file.upload-norbbs}")
    private String norBbsUploadDir;    // ../frontend/public/DATA/bbs/norBbs

    @Value("${file.upload-quesbbs}")
    private String quesBbsUploadDir;   // ../frontend/public/DATA/bbs/quesBbs

    // =========================
    // 🔧 /DATA/... → 물리경로(baseDir) 매핑 헬퍼
    // =========================
    private String resolveBaseDirByWebPath(String webPath) {
        if (webPath == null) return norBbsUploadDir; // 기본값
        if (webPath.contains("/DATA/bbs/imgBbs/"))  return imgBbsUploadDir;
        if (webPath.contains("/DATA/bbs/norBbs/"))  return norBbsUploadDir;
        if (webPath.contains("/DATA/bbs/quesBbs/")) return quesBbsUploadDir;
        return norBbsUploadDir; // fallback
    }

    // ---------------- 관리자용 공지사항 게시글 조회 (최신순) ----------------
    @GetMapping("/notices")
    public ResponseEntity<Map<String, Object>> getNoticeBbsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String bbscontent
    ) {
        BoardType type = BoardType.NORMAL;
        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }

    // ---------------- 관리자 게시글 작성 (NORMAL 게시판) ----------------
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<BbsDto> createBbs(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (type != BoardType.NORMAL)
            throw new IllegalArgumentException("관리자는 NORMAL 게시판만 작성할 수 있습니다.");

        dto.setBulletinType(type);

        // ✅ 본문 삽입 옵션 사전 필터 (이미지 외 insert 금지)
        if (files != null && insertOptions != null) {
            int size = Math.min(files.size(), insertOptions.size());
            for (int i = 0; i < size; i++) {
                MultipartFile file = files.get(i);
                String option = insertOptions.get(i);
                String filename = file.getOriginalFilename();
                String ext = (filename != null && filename.contains(".")) ?
                        filename.substring(filename.lastIndexOf(".") + 1).toLowerCase() : "";

                // 첨부 가능 파일 확장자 체크 (NORMAL/FAQ 정책에 맞춰 유지)
                if (!Arrays.asList("jpg", "jpeg", "png", "pdf", "ppt", "pptx", "doc", "docx").contains(ext)) {
                    insertOptions.set(i, "no-insert");
                    continue;
                }

                // 본문 삽입 가능 파일 체크 (이미지 계열만)
                if ("insert".equals(option)) {
                    if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))) {
                        insertOptions.set(i, "no-insert");
                    }
                }
            }
        }

        BbsDto created = bbsService.createBbs(dto, null, adminId, files, insertOptions, null);
        return ResponseEntity.ok(created);
    }

    // ---------------- Normal 게시글 단건 조회 ----------------
    @GetMapping("/normal/{id}")
    public ResponseEntity<BbsDto> getNormalBbsDetail(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String role = jwtTokenProvider.getRoleFromToken(token);
        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        BbsDto dto = bbsService.getBbs(id);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // ---------------- Normal 게시글 삭제 ----------------
    @DeleteMapping("/normal/{id}")
    public ResponseEntity<Void> deleteNormalBbs(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        BbsDto dto = bbsService.getBbs(id);
        if (dto == null || dto.getBulletinType() != BoardType.NORMAL)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Normal 게시글 첨부파일 조회 ----------------
    @GetMapping("/normal/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getNormalFiles(@PathVariable Long id) {
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();

        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath()); // ✅ 프론트가 원하면 직접 렌더 가능 (/DATA/..)
            // ✅ 보조용 다운로드 링크 (상세화면에서 "이름만 표시 + 클릭 시 다운로드" 용)
            fileMap.put("fileUrl", BACKEND_URL + "/admin/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        return ResponseEntity.ok(fileMapList);
    }

    // ---------------- 답변 작성 ----------------
    @PostMapping("/qna/{bbsId}/answer")
    public ResponseEntity<QandADto> saveQnaAnswer(
            @PathVariable Long bbsId,
            @RequestBody QandADto dto,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new QandADto());
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new QandADto());
        }

        QandADto savedDto = bbsService.saveQna(bbsId, dto, adminId);
        return ResponseEntity.ok(savedDto);
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
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);

        if (!"ADMIN".equals(role)) {
            throw new BbsException("삭제 권한이 없습니다.");
        }

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }
   

    
    // ---------------- 다중 삭제 ----------------
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleBbs(
            @RequestParam List<Long> ids,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        bbsService.deleteBbsMultiple(ids, null, adminId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- 관리자 일반 게시글 수정 ----------------
    @PutMapping(value = "/normal/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> updateAdminNormalBbs(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("bbsTitle") String bbsTitle,
            @RequestParam("bbsContent") String bbsContent,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deletedFileIds", required = false) String deletedFileIds,
            @RequestParam(value = "insertOptions", required = false) String insertOptionsCsv
    ) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            if (!jwtTokenProvider.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (!"ADMIN".equals(jwtTokenProvider.getRoleFromToken(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            Map<String, Object> response = new HashMap<>();

            List<Long> deletedIdsList = (deletedFileIds != null && !deletedFileIds.isBlank())
                    ? parseDeleteIds(deletedFileIds)
                    : new ArrayList<>();
            deletedIdsList.forEach(bbsService::deleteFileById);

            List<String> insertOptions = (insertOptionsCsv != null && !insertOptionsCsv.isBlank())
                    ? Arrays.asList(insertOptionsCsv.split(","))
                    : new ArrayList<>();

            BbsDto dto = new BbsDto();
            dto.setBbsTitle(bbsTitle);
            dto.setBbsContent(bbsContent);

            BbsDto updated = bbsService.updateBbs(
                    id,
                    dto,
                    null, // memberNum 생략
                    null, // adminId 생략
                    files,
                    deletedIdsList,
                    true,
                    insertOptions
            );
            response.put("bbs", updated);

            List<Map<String, Object>> fileMapList = new ArrayList<>();
            if (files != null) {
                for (MultipartFile f : files) {
                    Map<String, Object> fileMap = new HashMap<>();
                    fileMap.put("fileName", f.getOriginalFilename());
                    fileMap.put("size", f.getSize());
                    fileMapList.add(fileMap);
                }
            }
            response.put("files", fileMapList);

            return ResponseEntity.ok(response);

        } catch (BbsException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
 // ---------------- 관리자 이미지 게시글 수정 ----------------
    @PutMapping(value = "/poto/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> updateAdminPotoBbs(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("bbsTitle") String bbsTitle,
            @RequestParam("bbsContent") String bbsContent,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deletedFileIds", required = false) String deletedFileIds,
            @RequestParam(value = "overwriteFileIds", required = false) String overwriteFileIds,
            @RequestParam(value = "insertOptions", required = false) String insertOptionsCsv,
            @RequestParam(value = "isRepresentativeList", required = false) String isRepresentativeList
    ) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            if (!jwtTokenProvider.validateToken(token))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (!"ADMIN".equals(jwtTokenProvider.getRoleFromToken(token)))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            Map<String, Object> response = new HashMap<>();

            // 삭제/덮어쓰기 파일 ID 파싱
            List<Long> deleteIds = (deletedFileIds != null && !deletedFileIds.isBlank())
                    ? parseDeleteIds(deletedFileIds)
                    : new ArrayList<>();

            List<Long> overwriteIds = (overwriteFileIds != null && !overwriteFileIds.isBlank())
                    ? parseDeleteIds(overwriteFileIds)
                    : new ArrayList<>();

            if (!overwriteIds.isEmpty()) {
                deleteIds.addAll(overwriteIds);
            }

            // insertOptions
            List<String> insertOptions = (insertOptionsCsv != null && !insertOptionsCsv.isBlank())
                    ? Arrays.asList(insertOptionsCsv.split(","))
                    : new ArrayList<>();

            // 대표 이미지 ID
            List<Long> representativeIds = new ArrayList<>();
            if (isRepresentativeList != null && !isRepresentativeList.isBlank()) {
                try {
                    representativeIds.add(Long.valueOf(isRepresentativeList));
                } catch (NumberFormatException e) {
                    throw new BbsException("대표 이미지 ID가 유효하지 않습니다: " + isRepresentativeList);
                }
            }
            if (representativeIds.isEmpty()) {
                throw new BbsException("대표 이미지는 반드시 1장 선택해야 합니다.");
            }

            // DTO 준비
            BbsDto dto = new BbsDto();
            dto.setBbsTitle(bbsTitle);
            dto.setBbsContent(bbsContent);
            dto.setBulletinType(BoardType.POTO);

            // 서비스 호출 (관리자 전용)
            BbsDto updated = bbsService.updatePotoBbs(
                    id,
                    dto,
                    files,
                    representativeIds,
                    deleteIds,
                    overwriteIds,
                    null // 관리자 수정이므로 memberNum 없음
            );

            response.put("bbs", updated);

            // 대표 이미지 정보
            ImageBbsDto repImg = bbsService.getRepresentativeImage(updated.getBulletinNum());
            Map<String, Object> repImgMap = null;
            if (repImg != null) {
                repImgMap = new HashMap<>();
                repImgMap.put("bulletinNum", repImg.getBulletinNum());
                repImgMap.put("thumbnailPath", repImg.getThumbnailPath());
                repImgMap.put("imagePath", repImg.getImagePath());
            }
            response.put("representativeImage", repImgMap);

            // 첨부파일 리스트
            List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(updated.getBulletinNum());
            List<Map<String, Object>> fileMapList = new ArrayList<>();
            for (FileUpLoadDto f : filesList) {
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("fileNum", f.getFileNum());
                fileMap.put("originalName", f.getOriginalName());
                fileMap.put("savedName", f.getSavedName());
                fileMap.put("path", f.getPath());
                fileMap.put("size", f.getSize());
                fileMap.put("extension", f.getExtension());
                fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
                fileMapList.add(fileMap);
            }
            response.put("files", fileMapList);

            return ResponseEntity.ok(response);

        } catch (BbsException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ---------------- 관리자용 FAQ 게시글 조회 ----------------
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
        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }

    // ---------------- 관리자 QnA 게시글 단건 조회 ----------------
    @GetMapping("/qna/{id}")
    public ResponseEntity<Map<String, Object>> getQnaBbsDetail(@PathVariable Long id) {
        BbsDto bbsDto = bbsService.getBbs(id);
        QandADto qnaDto = bbsService.getQna(id);

        if (bbsDto == null) return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        response.put("bbs", bbsDto);
        response.put("answer", qnaDto != null ? qnaDto.getAnswer() : null);

        return ResponseEntity.ok(response);
    }

    // ---------------- 관리자용 이미지 게시글 조회 ----------------
    @GetMapping("/poto")
    public ResponseEntity<Map<String, Object>> getPotoBbsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String bbstitle,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String bbscontent
    ) {
        BoardType type = BoardType.POTO;
        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);

        List<BbsDto> content = (List<BbsDto>) result.get("list");
        if (content == null) content = new ArrayList<>();

        Map<String, Object> repImages = new HashMap<>();
        for (BbsDto dto : content) {
            var repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
            Map<String, Object> imgMap = new HashMap<>();
            if (repImg != null) {
                imgMap.put("bulletinNum", dto.getBulletinNum());
                imgMap.put("thumbnailPath", repImg.getThumbnailPath()); // ✅ /DATA/... 그대로 전달
                imgMap.put("imagePath", repImg.getImagePath());         // ✅ 프론트가 /DATA/... 로 직접 접근
            }
            repImages.put(dto.getBulletinNum().toString(), imgMap);
        }

        result.put("representativeImages", repImages);
        return ResponseEntity.ok(result);
    }

    // ---------------- 관리자 이미지 게시글 단건 조회 ----------------
    @GetMapping("/poto/{id}")
    public ResponseEntity<Map<String, Object>> getPotoBbsDetail(@PathVariable Long id) {
        BbsDto dto = bbsService.getBbs(id);

        // 대표 이미지
        ImageBbsDto repImg = bbsService.getRepresentativeImage(id);
        Map<String, Object> repImgMap = null;
        if (repImg != null) {
            repImgMap = new HashMap<>();
            repImgMap.put("bulletinNum", repImg.getBulletinNum());
            repImgMap.put("thumbnailPath", repImg.getThumbnailPath()); // ✅ /DATA/... 그대로
            repImgMap.put("imagePath", repImg.getImagePath());         // ✅ /DATA/... 그대로
        }

        // 첨부파일
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();
        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath()); // /DATA/... (직접 접근 가능)
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            // 보조 다운로드 링크
            fileMap.put("fileUrl", BACKEND_URL + "/admin/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        // 결과
        Map<String, Object> result = new HashMap<>();
        result.put("bbs", dto);
        result.put("representativeImage", repImgMap);
        result.put("files", fileMapList);

        return ResponseEntity.ok(result);
    }

    // ---------------- 관리자 게시글 첨부파일 조회 ----------------
    @GetMapping("/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getFilesByBbs(@PathVariable Long id) {
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();

        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath()); // /DATA/... (직접 접근 가능)
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            // 보조 다운로드 링크
            fileMap.put("fileUrl", BACKEND_URL + "/admin/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        return ResponseEntity.ok(fileMapList);
    }

    // ---------------- 첨부파일 다운로드 (보조용) ----------------
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        FileUpLoadDto fileDto = bbsService.getFileById(fileId);
        if (fileDto == null) return ResponseEntity.notFound().build();

        // ✅ DB path는 /DATA/... 이므로, 물리경로(baseDir)로 변환 후 savedName과 조합
        String baseDir = resolveBaseDirByWebPath(fileDto.getPath());
        Path path = Paths.get(baseDir, fileDto.getSavedName());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) return ResponseEntity.notFound().build();

        MediaType mediaType;
        String ext = fileDto.getExtension().toLowerCase();
        switch (ext) {
            case "jpeg":
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "ppt":
            case "pptx":
            case "doc":
            case "docx":
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        // 이미지 등 미리보기 가능한 타입은 inline, 그 외는 attachment
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok().contentType(mediaType);
        if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM) || mediaType.equals(MediaType.APPLICATION_PDF)) {
            responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileDto.getOriginalName() + "\"");
        }

        return responseBuilder.body(resource);
    }

    // ---------------- 본문 삽입 처리 (미사용: 서비스에서 처리) ----------------
    private String insertFilesToContent(String originalContent, List<FileUpLoadDto> files, List<String> insertOptions) {
        StringBuilder content = new StringBuilder(originalContent == null ? "" : originalContent);
        List<String> imageExt = List.of("jpg", "jpeg", "png"); // 본문 삽입 허용 이미지 확장자

        for (int i = 0; i < files.size(); i++) {
            FileUpLoadDto file = files.get(i);
            String option = (insertOptions != null && insertOptions.size() > i) ? insertOptions.get(i) : "no-insert";
            String ext = file.getExtension().toLowerCase();
            // ⚠️ 서비스에서 /DATA/... 을 직접 사용하도록 변경했으므로, 여기선 참고용
            String url = file.getPath();

            if ("insert".equals(option) && imageExt.contains(ext)) {
                content.append("\n<img src=\"")
                       .append(url)
                       .append("\" alt=\"")
                       .append(file.getOriginalName())
                       .append("\" style='max-width:600px;' />");
            }
        }

        return content.toString();
    }

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
}
