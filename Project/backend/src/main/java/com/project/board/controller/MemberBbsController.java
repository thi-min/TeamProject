package com.project.board.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // ✅ 추가: 물리 경로 주입용
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.BbsSimpleResponseDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.entity.QandAEntity;
import com.project.board.exception.BbsException;
import com.project.board.repository.QandARepository;
import com.project.board.service.BbsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bbs")
@RequiredArgsConstructor 
public class MemberBbsController {

    @Autowired  
    private BbsService bbsService;

    private final QandARepository qandARepository;

    // ⚠️ 프론트는 /DATA/... 로 직접 접근하므로 미리보기에는 BACKEND_URL을 붙이지 않음
    //    단, "다운로드" 클릭 시 호출할 보조용 링크 구성에만 사용
    private final String BACKEND_URL = "http://127.0.0.1:8090";

    // =========================
    // 📌 application.properties 값 주입 (물리 저장소 경로)
    //    downloadFile 에서 DB의 /DATA/... 를 물리경로로 매핑하는 데 사용
    //    application.properties 예:
    //      file.upload-imgbbs=../frontend/public/DATA/bbs/imgBbs
    //      file.upload-norbbs=../frontend/public/DATA/bbs/norBbs
    //      file.upload-quesbbs=../frontend/public/DATA/bbs/quesBbs
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

    // ---------------- 게시글 작성 ----------------
    @PostMapping("/bbslist/bbsadd")
    public ResponseEntity<Map<String, Object>> createBbs(
            @RequestParam Long memberNum,
            @RequestParam BoardType type,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions,
            @RequestParam(value = "isRepresentative", required = false) List<String> isRepresentativeList
    ) {
    	dto.setBulletinType(type);
    	
        // ✅ 본문 삽입은 이미지(jpg/jpeg/png)만 허용하도록 사전 필터
        if (files != null && insertOptions != null) {
            int size = Math.min(files.size(), insertOptions.size());
            for (int i = 0; i < size; i++) {
                MultipartFile file = files.get(i);
                String option = insertOptions.get(i);
                if ("insert".equals(option)) {
                    String filename = file.getOriginalFilename();
                    String ext = (filename != null && filename.contains(".")) ?
                            filename.substring(filename.lastIndexOf(".") + 1).toLowerCase() : "";
                    if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))) {
                        insertOptions.set(i, "no-insert");
                    }
                }
            }
        }

        BbsDto created;
        if (type == BoardType.POTO) {
            // 📸 이미지 게시판: 대표 이미지 필수
            created = bbsService.createPotoBbs(dto, memberNum, files, isRepresentativeList);
        } else {
            created = bbsService.createBbs(dto, memberNum, null, files, insertOptions, null);
        }

        // ---------------- Map 구조로 변환 ----------------
        Map<String, Object> response = new HashMap<>();
        response.put("bbs", created);

        // ✅ 대표 이미지(POTO 타입만) 조회 — 프론트가 /DATA/... 로 직접 접근하도록 반환
        ImageBbsDto repImg = null;
        Map<String, Object> repImgMap = null;
        if (type == BoardType.POTO) {
            repImg = bbsService.getRepresentativeImage(created.getBulletinNum());
            if (repImg != null) {
                repImgMap = new HashMap<>();
                repImgMap.put("bulletinNum", repImg.getBulletinNum());
                repImgMap.put("thumbnailPath", repImg.getThumbnailPath()); // /DATA/... 그대로
                repImgMap.put("imagePath", repImg.getImagePath());         // /DATA/... 그대로
            }
        }
        response.put("representativeImage", repImgMap);

        // ✅ 첨부파일 조회 (모든 게시판) — 프론트는 path(/DATA/...)로 미리보기 접근
        //    상세보기엔 파일명만 보여주고, 클릭시 아래 fileUrl(보조용)로 다운로드
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(created.getBulletinNum());
        List<Map<String, Object>> fileMapList = new ArrayList<>();
        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());  // 상세보기엔 이 텍스트만 보여주면 됨
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath());                  // /DATA/... (프론트 직접 접근용)
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download"); // 다운로드 보조용 링크
            fileMapList.add(fileMap);
        }
        response.put("files", fileMapList);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------- 게시글 수정 ----------------
 // ✅ 멀티파트 명시(consumes) + 숫자/문자열은 @RequestParam 으로 변경
    @PutMapping(value = "/member/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> updateBbsByType(
            @PathVariable Long id,
            // 🔁 숫자/문자열(텍스트 파트)은 @RequestParam 으로 받기 (octet-stream 이 와도 안전)
            @RequestParam("memberNum") Long memberNum,
            // 🔁 JSON 객체만 @RequestPart (application/json)
            @RequestPart("bbsDto") BbsDto dto,
            // 🔁 파일만 @RequestPart (file)
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            // 🔁 배열/리스트도 문자열(JSON)로 오므로 @RequestParam 으로 받기
            @RequestParam(value = "deletedFileIds", required = false) String deletedFileIds,
            @RequestParam(value = "overwriteFileIds", required = false) String overwriteFileIds,
            // (일반 게시판에서만 쓰는 옵션이지만 시그니처 유지)
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions,
            // 대표이미지 식별값(문자열) → @RequestParam
            @RequestParam(value = "isRepresentativeList", required = false) String isRepresentativeList,
            // 쿼리스트링/기본값 그대로 유지
            @RequestParam(value = "isAdmin", defaultValue = "false") boolean isAdmin,
            @RequestParam(value = "adminId", required = false) String adminId
    ) {
        try {
            Map<String, Object> response = new HashMap<>();

            if (dto.getBulletinType() == BoardType.POTO) {
                // ==================== 이미지 게시판 처리 ====================
                List<Long> deleteIds = (deletedFileIds != null && !deletedFileIds.isBlank())
                        ? parseDeleteIds(deletedFileIds)
                        : new ArrayList<>();

                List<Long> overwriteIds = (overwriteFileIds != null && !overwriteFileIds.isBlank())
                        ? parseDeleteIds(overwriteFileIds)
                        : new ArrayList<>();

                if (!overwriteIds.isEmpty()) {
                    deleteIds.addAll(overwriteIds); // 덮어쓰기는 삭제로 처리
                }

                List<MultipartFile> newFilesList = (files != null)
                        ? files.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList())
                        : Collections.emptyList();

                // 대표 이미지 id 처리
                List<Long> representativeIds = new ArrayList<>();
                if (isRepresentativeList != null && !isRepresentativeList.isBlank()) {
                    try {
                        representativeIds.add(Long.valueOf(isRepresentativeList));
                    } catch (NumberFormatException e) {
                        // 새 파일을 대표로 고르는 케이스는 이후 로직을 따로 마련해야 함
                        throw new BbsException("대표 이미지 ID가 유효하지 않습니다: " + isRepresentativeList);
                    }
                }
                if (representativeIds.isEmpty()) {
                    throw new BbsException("대표 이미지는 반드시 1장 선택해야 합니다.");
                }

                // Service 호출
                BbsDto updated = bbsService.updatePotoBbs(
                        id,
                        dto,
                        newFilesList,
                        representativeIds,
                        deleteIds,
                        overwriteIds,
                        memberNum
                );

                response.put("bbs", updated);

                // 대표 이미지
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

            } else {
                // ==================== 일반 게시판 처리(기존 로직 유지) ====================
                List<Long> deletedIdsList = deletedFileIds != null
                        ? parseDeleteIds(deletedFileIds)
                        : new ArrayList<>();

                BbsDto updated = bbsService.updateBbs(
                        id,
                        dto,
                        memberNum,
                        adminId,
                        files,
                        deletedIdsList,
                        isAdmin,
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
            }

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
        // 1. 게시글 정보 조회
        BbsDto dto = bbsService.getBbs(id);

        // 2. 대표 이미지(POTO 타입만) 조회 — /DATA/... 그대로 전달
        ImageBbsDto repImg = null;
        Map<String, Object> repImgMap = null;
        if (dto.getBulletinType() == BoardType.POTO) {
            repImg = bbsService.getRepresentativeImage(id);
            if (repImg != null) {
                repImgMap = new HashMap<>();
                repImgMap.put("bulletinNum", repImg.getBulletinNum());
                repImgMap.put("thumbnailPath", repImg.getThumbnailPath()); // /DATA/...
                repImgMap.put("imagePath", repImg.getImagePath());         // /DATA/...
            }
        }

        // 3. 첨부파일 조회 (모든 게시판)
        //    상세보기에는 파일명만 노출하고, 클릭 시 fileUrl로 다운로드
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();
        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath()); // /DATA/... (프론트 직접 접근 가능)
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        // 4. FAQ/Answer 처리
        String answer = null;
        if (dto.getBulletinType() == BoardType.FAQ) {
            Optional<QandAEntity> qaEntity = qandARepository.findByBbsBulletinNum(id);
            if (qaEntity.isPresent()) {
                answer = qaEntity.get().getAnswer();
            }
        }
        
        // 5. 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("bbs", dto);
        result.put("representativeImage", repImgMap);
        result.put("files", fileMapList);
        if (dto.getBulletinType() == BoardType.FAQ) {  
            result.put("answer", answer);  
        }
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
            // ✅ 기본 정렬을 registdate DESC로 명시
            @PageableDefault(size = 10, sort = "registdate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BbsDto> page = bbsService.searchPosts(searchType, bbstitle, bbscontent, memberName, type, pageable);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("content", page.getContent());
        pageMap.put("totalPages", page.getTotalPages());
        pageMap.put("number", page.getNumber());
        result.put("bbsList", pageMap);

        // (이하 기존 로직 그대로)
        if (type == BoardType.POTO) {
            Map<String, Object> repImageMap = new HashMap<>();
            page.getContent().forEach(dto -> {
                ImageBbsDto repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
                Map<String, Object> repMap = new HashMap<>();
                repMap.put("bulletinNum", dto.getBulletinNum());
                repMap.put("thumbnailPath", repImg != null ? repImg.getThumbnailPath() : "");
                repMap.put("imagePath", repImg != null ? repImg.getImagePath() : "");
                repImageMap.put(dto.getBulletinNum().toString(), repMap);
            });
            result.put("representativeImages", repImageMap);
        }

        return ResponseEntity.ok(result);
    }

    // ---------------- 첨부파일 조회 (상세에서 파일명 목록용) ----------------
    @GetMapping("/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getFilesByBbs(@PathVariable Long id) {
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();
        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());  // 상세에는 "이름만" 노출
            fileMap.put("path", f.getPath());                  // /DATA/... (필요 시 미리보기)
            fileMap.put("fileUrl", BACKEND_URL + "/bbs/files/" + f.getFileNum() + "/download"); // 다운로드 보조용
            fileMapList.add(fileMap);
        }
        return ResponseEntity.ok(fileMapList);
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

    // ---------------- 첨부파일 다운로드 (보조용) ----------------
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId,
                                                 @RequestParam(required = false) String boardType) {
        FileUpLoadDto fileDto = bbsService.getFileById(fileId);
        if (fileDto == null) return ResponseEntity.notFound().build();

        // ✅ POTO 전용 제약: 이미지 게시판에서는 jpg/jpeg만 허용
        if ("image".equals(boardType)) {
            String extCheck = fileDto.getExtension().toLowerCase();
            if (!extCheck.equals("jpg") && !extCheck.equals("jpeg")) {
                return ResponseEntity.badRequest().build();
            }
        }

        // ✅ DB path는 /DATA/... 이므로, 물리경로(baseDir)로 변환 후 savedName과 조합
        String baseDir = resolveBaseDirByWebPath(fileDto.getPath());
        Path path = Paths.get(baseDir, fileDto.getSavedName());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 미디어 타입 판별 (png 추가)
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
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok().contentType(mediaType);
        if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM) || mediaType.equals(MediaType.APPLICATION_PDF)) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileDto.getOriginalName() + "\"");
        }

        return builder.body(resource);
    }

    // 게시판 공지사항 조회 최신5개 (안형주 추가 09.01)
    @GetMapping("/latest")
    public List<BbsSimpleResponseDto> getLatestNormalPosts() {
        return bbsService.getLatestNormalPosts();
    }
}
