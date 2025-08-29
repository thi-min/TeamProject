package com.project.board.controller;

import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.QandADto;
import com.project.board.exception.BbsException;
import com.project.board.service.BbsService;
import com.project.common.jwt.JwtTokenProvider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/admin/bbs")
public class BbsAdminController {

	@Autowired  
    private BbsService bbsService;
	@Autowired 
	private JwtTokenProvider jwtTokenProvider;

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
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (type != BoardType.NORMAL) {
            throw new IllegalArgumentException("관리자는 NORMAL 게시판만 작성할 수 있습니다.");
        }

        // ✅ bulletinType null 방지
        dto.setBulletinType(type);

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

 // ---------------- 관리자 Normal 게시글 단건 조회 ----------------
    @GetMapping("/normal/{id}")
    public ResponseEntity<BbsDto> getNormalBbsDetail(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            BbsDto dto = bbsService.getBbs(id); // Normal 게시글 조회
            if (dto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 // ---------------- 관리자 NORMAL 게시판 단건 삭제 ----------------
    @DeleteMapping("/normal/{id}")
    public ResponseEntity<Void> deleteNormalBbs(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BbsDto dto = bbsService.getBbs(id);
        if (dto == null || dto.getBulletinType() != BoardType.NORMAL) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build(); // NORMAL 게시판이 아닌 경우 삭제 불가
        }

        bbsService.deleteBbs(id, null, adminId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/normal/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getNormalFiles(@PathVariable Long id) {
        List<FileUpLoadDto> filesList = bbsService.getFilesByBbs(id);
        List<Map<String, Object>> fileMapList = new ArrayList<>();

        for (FileUpLoadDto f : filesList) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileNum", f.getFileNum());
            fileMap.put("originalName", f.getOriginalName());
            fileMap.put("savedName", f.getSavedName());
            fileMap.put("path", f.getPath());
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            fileMap.put("fileUrl", "http://127.0.0.1:8090/admin/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        return ResponseEntity.ok(fileMapList);
    }

    //답변 작성
    @PostMapping("/qna/{bbsId}/answer")
    public ResponseEntity<QandADto> saveQnaAnswer(
            @PathVariable Long bbsId,
            @RequestBody QandADto dto,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new QandADto()); // 빈 DTO 반환
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new QandADto()); // 빈 DTO 반환
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

 // ---------------- 게시글 단건 삭제 (JWT) ----------------
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

        bbsService.deleteBbs(id, null, adminId); // ← requesterAdminId 전달
        return ResponseEntity.noContent().build();
    }


    // ---------------- 다중 삭제 (JWT) ----------------
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

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        bbsService.deleteBbsMultiple(ids, null, adminId);
        return ResponseEntity.noContent().build();
    }


    // ---------------- 관리자 게시글 수정 ----------------
    @PutMapping("/admin/{id}")
    public ResponseEntity<BbsDto> updateAdminBbs(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("bbsDto") BbsDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(required = false) String deletedFileIds,
            @RequestParam(value = "insertOptions", required = false) List<String> insertOptions
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtTokenProvider.getRoleFromToken(token);
        String adminId = jwtTokenProvider.getMemberIdFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Long> deleteIds = parseDeleteIds(deletedFileIds);
        
        // 파일 insertOptions 처리 (이미지 여부)
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

        BbsDto updated = bbsService.updateBbs(id, dto, null, adminId, files, deleteIds, true, insertOptions);
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
        Map<String, Object> result = bbsService.getBbsList(type, page, size, bbstitle, memberName, bbscontent);
        return ResponseEntity.ok(result);
    }
    // ---------------- 관리자 QnA 게시글 단건 조회 ----------------
 // 관리자 QnA 게시글 단건 조회 (BbsDto + answer)
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




    // ---------------- 관리자용 이미지 게시글 조회 (최신순) ----------------
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

        // ---------------- bbsList에서 content 꺼내기 ----------------
        List<BbsDto> content = (List<BbsDto>) result.get("list");  // <-- key 수정
        if (content == null) content = new ArrayList<>();

        // ---------------- 대표 이미지 추가 ----------------
        Map<String, Object> repImages = new HashMap<>();
        for (BbsDto dto : content) {
            var repImg = bbsService.getRepresentativeImage(dto.getBulletinNum());
            Map<String, Object> imgMap = new HashMap<>();
            if (repImg != null) {
                imgMap.put("bulletinNum", dto.getBulletinNum());
                imgMap.put("thumbnailPath", repImg.getThumbnailPath());
                imgMap.put("imagePath", repImg.getImagePath() != null ? "http://127.0.0.1:8090" + repImg.getImagePath() : "");
            }
            repImages.put(dto.getBulletinNum().toString(), imgMap);
        }

        result.put("representativeImages", repImages);
        return ResponseEntity.ok(result);
    }




    // 관리자 이미지 게시글 단건 조회
    @GetMapping("/poto/{id}")
    public ResponseEntity<BbsDto> getPotoBbsDetail(@PathVariable Long id) {
        BbsDto dto = bbsService.getBbs(id);
        return ResponseEntity.ok(dto);
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
            fileMap.put("path", f.getPath());
            fileMap.put("size", f.getSize());
            fileMap.put("extension", f.getExtension());
            fileMap.put("fileUrl", "http://127.0.0.1:8090/admin/bbs/files/" + f.getFileNum() + "/download");
            fileMapList.add(fileMap);
        }

        return ResponseEntity.ok(fileMapList);
    }

    // ---------------- 관리자 게시글 첨부파일 다운로드 ----------------
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

}

