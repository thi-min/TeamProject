//package com.project.board.service;
//
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.UUID;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.project.admin.entity.AdminEntity;
//import com.project.admin.repository.AdminRepository;
//import com.project.board.BoardType;
//import com.project.board.dto.BbsDto;
//import com.project.board.dto.FileUpLoadDto;
//import com.project.board.dto.ImageBbsDto;
//import com.project.board.dto.QandADto;
//import com.project.board.entity.BbsEntity;
//import com.project.board.entity.FileUpLoadEntity;
//import com.project.board.entity.ImageBbsEntity;
//import com.project.board.entity.QandAEntity;
//import com.project.board.exception.BbsException;
//import com.project.board.repository.BbsRepository;
//
//
//import com.project.board.repository.FileUpLoadRepository;
//import com.project.board.repository.ImageBbsRepository;
//import com.project.board.repository.QandARepository;
//import com.project.member.entity.MemberEntity;
//import com.project.member.repository.MemberRepository;
//
//import lombok.RequiredArgsConstructor;
//
//
//
//@Service
//@RequiredArgsConstructor
//public class BbsServiceImpl2 implements BbsService {
//
//	private final BbsRepository bbsRepository;
//    private final QandARepository qandARepository;
//    private final ImageBbsRepository imageBbsRepository;
//    private final FileUpLoadRepository fileUploadRepository;
//    private final MemberRepository memberRepository;
//    private final AdminRepository adminRepository;
// 
//    
//    private final String uploadDir = "C:/photo"; // 공통 업로드 경로
//
//    // ---------------- 게시글 저장 ----------------
//    private BbsDto saveOnlyBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId) {
//        MemberEntity member = null;
//
//        // ✅ memberNum이 null이면 엔티티에 세팅하지 않음
//        if (dto.getMemberNum() != null) {
//            member = memberRepository.findByMemberNum(dto.getMemberNum())
//                    .orElseThrow(() -> new BbsException("회원이 존재하지 않습니다."));
//        }
//
//        BbsEntity.BbsEntityBuilder builder = BbsEntity.builder()
//                .bulletinNum(dto.getBulletinNum())
//                .bbstitle(dto.getBbsTitle())
//                .bbscontent(dto.getBbsContent())
//                .registdate(LocalDateTime.now())
//                .revisiondate(dto.getRevisionDate())
//                .deldate(dto.getDelDate())
//                .viewers(dto.getViewers() != null ? dto.getViewers() : 0)
//                .bulletinType(dto.getBulletinType());
//
//        if (member != null) {
//            builder.memberNum(member);
//        }
//
//        BbsEntity entity = builder.build();
//        BbsEntity savedEntity = bbsRepository.save(entity);
//
//        return convertToDto(savedEntity);
//    }
//
//    
//
//    // ---------------- 최상위 생성 메소드 ----------------
//    @Override
//    @Transactional
//    public BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId,
//                            List<MultipartFile> files, List<String> insertOptions,
//                            List<String> isRepresentativeList) {
//
//        BoardType type = dto.getBulletinType();
//
//        // 로그인 상태 확인
//        if ((type == BoardType.FAQ || type == BoardType.POTO) && requesterMemberNum == null) {
//            throw new BbsException("로그인 후 작성 가능합니다.");
//        }
//
//        // 권한 체크 (공지사항)
//        if (type == BoardType.NORMAL && requesterAdminId == null) {
//            throw new BbsException("공지사항은 관리자만 작성할 수 있습니다.");
//        }
//
//        // DTO에 memberNum 설정
//        dto.setMemberNum(requesterMemberNum);
//
//        // 게시글 저장 (첨부파일 제외)
//        BbsDto savedDto = saveOnlyBbs(dto, requesterMemberNum, requesterAdminId);
//
//        if (type == BoardType.POTO) {
//            return createPotoBbs(savedDto, requesterMemberNum, files, isRepresentativeList);
//        } else {
//            BbsDto result = createBbsWithFiles(savedDto, requesterMemberNum, requesterAdminId, files, insertOptions);
//
//            // 본문 삽입 이미지 처리
//            if (files != null && insertOptions != null) {
//                StringBuilder contentBuilder = new StringBuilder(
//                        result.getBbsContent() == null ? "" : result.getBbsContent()
//                );
//
//                List<FileUpLoadDto> savedFiles = getFilesByBbs(result.getBulletinNum());
//                int size = Math.min(savedFiles.size(), insertOptions.size());
//
//                for (int i = 0; i < size; i++) {
//                    String option = insertOptions.get(i);
//                    if ("insert".equals(option)) {
//                        FileUpLoadDto fileDto = savedFiles.get(i);
//                        String fileUrl = "http://127.0.0.1:8090/bbs/files/" + fileDto.getFileNum() + "/download";
//                        contentBuilder.append("<br><img src='")
//                                      .append(fileUrl)
//                                      .append("' style='max-width:600px;'/>");
//                    }
//                }
//
//                // 본문 업데이트 후 다시 저장
//                result.setBbsContent(contentBuilder.toString());
//                saveOnlyBbs(result, requesterMemberNum, requesterAdminId);
//            }
//
//            return result;
//        }
//    }
//
//
//
//    
//    @Override
//    public FileUpLoadDto getFileById(Long fileId) {
//        return bbsRepository.findFileById(fileId)
//                .orElseThrow(() -> new BbsException("해당 파일이 존재하지 않습니다. ID: " + fileId));
//    }
//   
//
// // ---------------- POTO 게시판 처리 ----------------
//    @Override
//    @Transactional
//    public BbsDto createPotoBbs(BbsDto dto, Long requesterMemberNum,
//                                List<MultipartFile> files, List<String> isRepresentativeList) {
//
//        if (files == null || files.isEmpty()) {
//            throw new BbsException("이미지 게시판은 최소 1장 이상의 사진을 등록해야 합니다.");
//        }
//
//        MemberEntity member = memberRepository.findById(requesterMemberNum)
//                .orElseThrow(() -> new BbsException("회원 정보가 존재하지 않습니다."));
//
//        // 게시글 먼저 저장
//        BbsEntity savedEntity = BbsEntity.builder()
//                .bbstitle(dto.getBbsTitle())
//                .bbscontent(dto.getBbsContent())
//                .bulletinType(dto.getBulletinType())
//                .memberNum(member)
//                .registdate(LocalDateTime.now())
//                .viewers(0)
//                .build();
//        savedEntity = bbsRepository.save(savedEntity);
//
//        if (isRepresentativeList == null || isRepresentativeList.size() != files.size()) {
//            throw new BbsException("대표 이미지 정보가 올바르지 않습니다.");
//        }
//
//        ImageBbsEntity representativeImage = null;
//        List<String> allowedExtensions = List.of("jpg", "jpeg");
//        List<String> allowedMimeTypes = List.of("image/jpeg");
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
//            if (file == null || file.isEmpty()) continue;
//
//            String ext = getExtension(file.getOriginalFilename());
//            String contentType = file.getContentType();
//
//            if (ext == null || !allowedExtensions.contains(ext.toLowerCase())
//                    || contentType == null || !allowedMimeTypes.contains(contentType.toLowerCase())
//                    || file.getSize() > 5 * 1024 * 1024) {
//                throw new BbsException("첨부파일은 jpg 또는 jpeg 이미지만 가능합니다. (" + file.getOriginalFilename() + ")");
//            }
//
//            String savedName = UUID.randomUUID() + "." + ext;
//            Path target = Paths.get(uploadDir, savedName);
//
//            try {
//                file.transferTo(target);
//            } catch (IOException e) {
//                throw new BbsException("이미지 저장 실패: " + file.getOriginalFilename(), e);
//            }
//
//            boolean isRep = "Y".equalsIgnoreCase(isRepresentativeList.get(i));
//            if (isRep && representativeImage == null) {
//                representativeImage = ImageBbsEntity.builder()
//                        .bbs(savedEntity)
//                        .thumbnailPath("/uploads/thumb/" + savedName)
//                        .imagePath("/uploads/" + savedName)
//                        .build();
//                imageBbsRepository.save(representativeImage);
//            }
//        }
//
//        if (representativeImage == null) {
//            throw new BbsException("대표 이미지가 반드시 필요합니다.");
//        }
//
//        saveFileList(savedEntity.getBulletinNum(), files, BoardType.POTO);
//
//        return dto;
//    }
//
//    // ---------------- 일반 게시판 파일 처리 ----------------
//    @Transactional
//    public BbsDto createBbsWithFiles(BbsDto savedBbs, Long requesterMemberNum, Long requesterAdminId,
//                                    List<MultipartFile> files, List<String> insertOptions) {
//
//        if (files != null && !files.isEmpty()) {
//            // 1️⃣ 파일 저장
//            List<FileUpLoadDto> uploadedFiles = saveFileList(savedBbs.getBulletinNum(), files, savedBbs.getBulletinType());
//
//            // 2️⃣ insertOptions 기반 본문 삽입
//            String updatedContent = insertFilesToContent(savedBbs.getBbsContent(), uploadedFiles, insertOptions);
//
//            // 3️⃣ 게시글 엔티티 업데이트
//            BbsEntity bbsEntity = bbsRepository.findById(savedBbs.getBulletinNum())
//                    .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다."));
//            bbsEntity.setBbscontent(updatedContent);
//            bbsRepository.save(bbsEntity);
//
//            savedBbs.setBbsContent(updatedContent);
//        }
//
//        return savedBbs;
//    }
//
//    // ---------------- 본문 삽입 처리 ----------------
//    private String insertFilesToContent(String originalContent, List<FileUpLoadDto> files, List<String> insertOptions) {
//        StringBuilder content = new StringBuilder(originalContent == null ? "" : originalContent);
//        List<String> imageExt = List.of("jpg", "jpeg");
//
//        for (int i = 0; i < files.size(); i++) {
//            FileUpLoadDto file = files.get(i);
//            String option = (insertOptions != null && insertOptions.size() > i) ? insertOptions.get(i) : "no-insert";
//            String ext = file.getExtension().toLowerCase();
//            String url = "/uploads/" + file.getSavedName();
//
//            if ("insert".equals(option) && imageExt.contains(ext)) {
//                content.append("\n<img src=\"").append(url).append("\" alt=\"").append(file.getOriginalName()).append("\" />");
//            } else if (!imageExt.contains(ext)) { // 문서류는 항상 링크
//                content.append("\n<a href=\"").append(url).append("\" download>").append(file.getOriginalName()).append("</a>");
//            }
//        }
//
//        return content.toString();
//    }
//
//    // ---------------- 게시글 수정 ----------------
//    @Override
//    @Transactional
//    public BbsDto updateBbs(Long id, BbsDto dto, Long userId, List<MultipartFile> newFiles, List<Long> deleteFileIds, boolean isAdmin, List<String> insertOptions) {
//        BbsEntity bbs = bbsRepository.findById(id)
//                .orElseThrow(() -> new BbsException("게시글 없음: " + id));
//
//        // 권한 체크
//        if (isAdmin && (bbs.getAdminId() == null || !bbs.getAdminId().getAdminId().equals(userId))) {
//            throw new BbsException("관리자 권한이 없습니다.");
//        }
//        if (!isAdmin && (bbs.getMemberNum() == null || !bbs.getMemberNum().getMemberNum().equals(userId))) {
//            throw new BbsException("본인이 작성한 글만 수정 가능합니다.");
//        }
//
//        // 게시글 업데이트
//        bbs.setBbstitle(dto.getBbsTitle());
//        bbs.setBbscontent(dto.getBbsContent());
//        bbs.setRevisiondate(dto.getRevisionDate());
//
//        // 삭제 파일 처리
//        if (deleteFileIds != null) {
//            for (Long fileId : deleteFileIds) deleteFileById(fileId);
//        }
//
//        // 새 파일 업로드 (본문 삽입 옵션 반영)
//        if (newFiles != null && !newFiles.isEmpty()) {
//            createBbsWithFiles(convertToDto(bbs), isAdmin ? null : userId, isAdmin ? userId : null, newFiles, insertOptions);
//        }
//
//        return convertToDto(bbsRepository.save(bbs));
//    }
//
//    // ---------------- 게시글 단일 삭제 ----------------
//    @Override
//    @Transactional
//    public void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId) {
//        BbsEntity bbs = bbsRepository.findById(id).orElseThrow(() -> new BbsException("게시글 없음: " + id));
//
//        boolean isAdmin = requesterAdminId != null;
//        boolean isAuthor = requesterMemberNum != null && bbs.getMemberNum() != null && requesterMemberNum.equals(bbs.getMemberNum().getMemberNum());
//
//        if (!(isAdmin || isAuthor)) throw new BbsException("삭제 권한이 없습니다.");
//
//        // QnA, 첨부파일, 이미지 삭제 처리
//        if (bbs.getBulletinType() == BoardType.FAQ) qandARepository.deleteByBbsBulletinNum(id);
//        deleteFilesAndImages(bbs);
//
//        bbsRepository.deleteById(id);
//    }
//
//    // ---------------- 다중 삭제 ----------------
//    @Override
//    @Transactional
//    public void deleteBbsMultiple(List<Long> ids, Long requesterMemberNum, Long requesterAdminId) {
//        if (requesterAdminId == null) throw new BbsException("관리자 권한이 필요합니다.");
//
//        for (Long id : ids) {
//            BbsEntity bbs = bbsRepository.findById(id).orElseThrow(() -> new BbsException("게시글 없음: " + id));
//            if (bbs.getBulletinType() == BoardType.FAQ) qandARepository.deleteByBbsBulletinNum(id);
//            deleteFilesAndImages(bbs);
//            bbsRepository.deleteById(id);
//        }
//    }
//
//    // ---------------- 첨부파일 + POTO 이미지 삭제 공통 ----------------
//    private void deleteFilesAndImages(BbsEntity bbs) {
//        // 첨부파일 삭제
//        List<FileUpLoadEntity> files = fileUploadRepository.findByBbsBulletinNum(bbs.getBulletinNum());
//        for (FileUpLoadEntity file : files) {
//            try { Files.deleteIfExists(Paths.get(uploadDir, file.getSavedName())); }
//            catch (IOException ignored) {}
//        }
//        fileUploadRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());
//
//        // POTO 이미지 삭제
//        if (bbs.getBulletinType() == BoardType.POTO) {
//            List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bbs.getBulletinNum());
//            for (ImageBbsEntity image : images) {
//                try {
//                    if (image.getThumbnailPath() != null) Files.deleteIfExists(Paths.get(uploadDir, Paths.get(image.getThumbnailPath()).getFileName().toString()));
//                    if (image.getImagePath() != null) Files.deleteIfExists(Paths.get(uploadDir, Paths.get(image.getImagePath()).getFileName().toString()));
//                } catch (IOException ignored) {}
//            }
//            imageBbsRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());
//        }
//    }
//
//
// // 게시글 단건 조회
//    @Override
//    public BbsDto getBbs(Long id) {
//        BbsEntity entity = bbsRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("게시글 없음: " + id));
//        return convertToDto(entity); // BbsDto만 반환
//    }
//
//
//    @Override
//    public List<BbsDto> getAllByType(BoardType type) {
//        return bbsRepository.findByBulletinType(type).stream()
//            .map(this::convertToDto)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    public Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable) {
//        Sort sorted = "views".equals(sort) ? Sort.by("viewers").descending() : Sort.by("registdate").descending();
//        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorted);
//        Page<BbsEntity> page = (type != null)
//            ? bbsRepository.findByBulletinType(type, sortedPageable)
//            : bbsRepository.findAll(sortedPageable);
//        return page.map(this::convertToDto);
//    }
//
// // 게시글 목록 조회
//    @Override
//    public Page<BbsDto> searchPosts(String searchType, String bbstitle, String bbscontent,
//                                    String memberName, BoardType type, Pageable pageable) {
//        Page<BbsEntity> result;
//
//        String typeLower = (searchType == null || searchType.isEmpty()) ? "all" : searchType.toLowerCase();
//
//        if (type != null) {
//            result = switch (typeLower) {
//                case "title" -> bbsRepository.findByBulletinTypeAndBbstitleContaining(type, bbstitle, pageable);
//                case "content" -> bbsRepository.findByBulletinTypeAndBbscontentContaining(type, bbscontent, pageable);
//                case "all" -> bbsRepository.findByBulletinType(type, pageable);
//                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
//            };
//        } else {
//            result = switch (typeLower) {
//                case "title" -> bbsRepository.findByBbstitleContaining(bbstitle, pageable);
//                case "content" -> bbsRepository.findByBbscontentContaining(bbscontent, pageable);
//                case "all" -> bbsRepository.findAll(pageable);
//                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
//            };
//        }
//
//        return result.map(this::convertToDto);
//    }
//
//    @Override
//    public ImageBbsDto getRepresentativeImage(Long bulletinNum) {
//        // 1. 대표 이미지 조회
//        String imagePath = imageBbsRepository.findRepresentativeImagePath(bulletinNum);
//
//        // 2. 대표 이미지가 없으면 첫 번째 이미지 사용
//        if (imagePath == null) {
//            List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bulletinNum);
//            if (images.isEmpty()) return null; // 이미지 자체가 없는 경우
//            imagePath = images.get(0).getImagePath(); // 첫 번째 이미지
//        }
//
//        // 3. DTO 반환
//        return ImageBbsDto.builder()
//                .bulletinNum(bulletinNum)
//                .thumbnailPath(null)
//                .imagePath("/uploads/" + imagePath)
//                .build();
//    }
//
//
//
//
//
//
//    private BbsDto convertToDto(BbsEntity e) {
//        String originalName = null;
//        String filteredName = null;
//        if (e.getMemberNum() != null) {
//            originalName = e.getMemberNum().getMemberName();
//            filteredName = filterName(originalName);
//        } else if (e.getAdminId() != null) {
//            originalName = e.getAdminId().getAdminName();
//            filteredName = originalName; // 관리자 이름은 마스킹 안 해도 되면 그대로 사용
//        } else {
//            filteredName = "알 수 없음";
//        }
//
//        return BbsDto.builder()
//                .bulletinNum(e.getBulletinNum())
//                .bbsTitle(e.getBbstitle())
//                .bbsContent(e.getBbscontent())
//                .registDate(e.getRegistdate())
//                .revisionDate(e.getRevisiondate())
//                .delDate(e.getDeldate())
//                .viewers(e.getViewers())
//                .bulletinType(e.getBulletinType())
//                .adminId(e.getAdminId() != null ? e.getAdminId().getAdminId() : null)
//                .memberNum(e.getMemberNum() != null ? e.getMemberNum().getMemberNum() : null)
//                .memberName(filteredName)
//                .build();
//    }
//
//    private String filterName(String name) {
//        if (name == null || name.length() < 2) return name;
//        int len = name.length();
//        if (len == 2) return name.charAt(0) + "*";
//        StringBuilder sb = new StringBuilder();
//        sb.append(name.charAt(0));
//        for (int i = 1; i < len - 1; i++) sb.append("*");
//        sb.append(name.charAt(len - 1));
//        return sb.toString();
//    }
//
//    @Transactional
//    @Override
//    public QandADto saveQna(Long bbsId, QandADto dto, String requesterAdminId) {
//        if (requesterAdminId == null) {
//            throw new BbsException("QnA 답변은 관리자만 작성할 수 있습니다.");
//        }
//
//        BbsEntity bbs = bbsRepository.findById(bbsId)
//            .orElseThrow(() -> new BbsException("게시글 없음"));
//
//        AdminEntity adminEntity = adminRepository.findFirstByAdminId(requesterAdminId)
//                .orElseThrow(() -> new RuntimeException("관리자 없음"));
//
//        QandAEntity entity = QandAEntity.builder()
//            .bbs(bbs)
//            .question(dto.getQuestion())
//            .answer(dto.getAnswer())
//            .build();
//
//        QandAEntity saved = qandARepository.save(entity);
//
//        return QandADto.builder()
//            .bulletinNum(saved.getBbs().getBulletinNum())
//            .question(saved.getQuestion())
//            .answer(saved.getAnswer())
//            .build();
//    }
//
//    @Override
//    public QandADto getQna(Long bbsId) {
//        QandAEntity qna = qandARepository.findByBbsBulletinNum(bbsId)
//            .orElseThrow(() -> new BbsException("QnA 없음"));
//
//        return QandADto.builder()
//            .bulletinNum(qna.getBbs().getBulletinNum())
//            .question(qna.getQuestion())
//            .answer(qna.getAnswer())
//            .build();
//    }
//    
//    @Override
//    public void deleteQna(Long qnaId, Long adminId) {
//        QandAEntity qna = qandARepository.findById(qnaId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 QnA 답변이 존재하지 않습니다."));
//
//        // 관리자 권한 체크 (필요하면 로직 추가)
//        // 예: adminId가 null이거나, 권한 없는 경우 예외 처리
//        if (adminId == null) {
//            throw new IllegalArgumentException("관리자 ID가 필요합니다.");
//        }
//
//        qandARepository.delete(qna);
//    }
//
//    @Override
//    public QandADto updateQna(Long qnaId, QandADto dto) {
//        QandAEntity qna = qandARepository.findById(qnaId)
//            .orElseThrow(() -> new BbsException("QnA 없음"));
//
//        qna.setQuestion(dto.getQuestion());
//        qna.setAnswer(dto.getAnswer());
//
//        return QandADto.builder()
//            .bulletinNum(qna.getBbs().getBulletinNum())
//            .question(qna.getQuestion())
//            .answer(qna.getAnswer())
//            .build();
//    }
//    
//  /*  @Override
//    public List<ImageBbsDto> saveImageFileList(Long bbsId, List<MultipartFile> files) {
//        BbsEntity bbs = bbsRepository.findById(bbsId)
//            .orElseThrow(() -> new BbsException("게시글 없음"));
//
//        List<String> allowedExtensions = List.of("jpg", "jpeg");
//        List<String> allowedMimeTypes = List.of("image/jpeg");
//        long maxSize = 5 * 1024 * 1024;
//        String uploadDir = "C:/Image"; // 실제 업로드 경로
//
//        List<ImageBbsEntity> imageEntities = files.stream()
//            .filter(file -> {
//                String ext = getExtension(file.getOriginalFilename());
//                String contentType = file.getContentType();
//                return file != null && !file.isEmpty()
//                        && ext != null && allowedExtensions.contains(ext.toLowerCase())
//                        && contentType != null && allowedMimeTypes.contains(contentType.toLowerCase())
//                        && file.getSize() <= maxSize;
//            })
//            .map(file -> {
//                String ext = getExtension(file.getOriginalFilename());
//                String savedName = UUID.randomUUID().toString() + "." + ext;
//                Path target = Paths.get(uploadDir, savedName);
//
//                try {
//                    file.transferTo(target);
//                } catch (IOException e) {
//                    throw new BbsException("이미지 저장 실패: " + file.getOriginalFilename(), e);
//                }
//
//                return ImageBbsEntity.builder()
//                    .bbs(bbs)
//                    .thumbnailPath("/uploads/thumb/" + savedName) // 썸네일 경로 구성
//                    .imagePath("/uploads/" + savedName)           // 원본 이미지 경로 구성
//                    .build();
//            })
//            .collect(Collectors.toList());
//
//        if (imageEntities.isEmpty()) {
//            throw new BbsException("유효한 이미지 파일(jpg, jpeg)만 첨부할 수 있습니다.");
//        }
//
//        return imageBbsRepository.saveAll(imageEntities).stream()
//            .map(entity -> ImageBbsDto.builder()
//                .bulletinNum(entity.getBbs().getBulletinNum())
//                .thumbnailPath(entity.getThumbnailPath())
//                .imagePath(entity.getImagePath())
//                .build())
//            .collect(Collectors.toList());
//    } */
//
//    @Override
//    public List<ImageBbsDto> getImageBbsList(Long bbsId) {
//        return imageBbsRepository.findByBbsBulletinNum(bbsId).stream()
//            .map(entity -> ImageBbsDto.builder()
//                .bulletinNum(entity.getBbs().getBulletinNum())
//                .thumbnailPath(entity.getThumbnailPath())
//                .imagePath(entity.getImagePath())
//                .build())
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public ImageBbsDto updateImage(Long bulletinNum, ImageBbsDto dto, MultipartFile newFile) {
//        // bulletinNum 으로 이미지 리스트 조회
//        List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bulletinNum);
//
//        if (images.isEmpty()) {
//            throw new BbsException("해당 게시글에 이미지가 없습니다.");
//        }
//
//        // 우선 첫 번째 이미지 선택 (특정 이미지 지정 로직이 없으므로)
//        ImageBbsEntity image = images.get(0);
//
//        if (newFile != null && !newFile.isEmpty()) {
//            try {
//                String ext = getExtension(newFile.getOriginalFilename());
//                String savedName = UUID.randomUUID().toString() + "." + ext;
//                String uploadDir = "C:/photo";
//                Path savedPath = Paths.get(uploadDir, savedName);
//
//                newFile.transferTo(savedPath);
//
//                String dbImagePath = "/uploads/" + savedName;
//                String dbThumbnailPath = "/uploads/thumb_" + savedName;
//
//                image.setImagePath(dbImagePath);
//                image.setThumbnailPath(dbThumbnailPath);
//            } catch (IOException e) {
//                throw new BbsException("이미지 파일 저장 실패", e);
//            }
//        } else {
//            image.setImagePath(dto.getImagePath());
//            image.setThumbnailPath(dto.getThumbnailPath());
//        }
//
//        return ImageBbsDto.builder()
//            .bulletinNum(image.getBbs().getBulletinNum())
//            .thumbnailPath(image.getThumbnailPath())
//            .imagePath(image.getImagePath())
//            .build();
//    }
//
//
//
//    @Override
//    public List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType) {
//        BbsEntity bbs = bbsRepository.findById(bbsId)
//            .orElseThrow(() -> new BbsException("해당 게시글이 존재하지 않습니다"));
//
//        // 게시판 유형별 허용 확장자 및 MIME 타입 정의
//        List<String> allowedExtensions;
//        List<String> allowedMimeTypes;
//
//        switch (boardType) {
//            case POTO:
//
//                allowedExtensions = List.of("jpg", "jpeg");
//
//                allowedMimeTypes = List.of("image/jpeg");
//
//                break; 
//            case FAQ:
//            case NORMAL:
//                allowedExtensions = List.of("jpg", "jpeg", "pdf", "ppt", "pptx", "doc", "docx");
//                allowedMimeTypes = List.of(
//                    "image/jpeg",
//                    "application/pdf",
//                    "application/vnd.ms-powerpoint", // ppt
//                    "application/vnd.openxmlformats-officedocument.presentationml.presentation", // pptx
//                    "application/msword", // doc
//                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // docx
//                );
//                break;
//            default:
//                throw new BbsException("지원하지 않는 게시판 타입입니다.");
//        }
//
//        long maxSize = 5 * 1024 * 1024; // 5MB
//        String uploadDir = "C:/photo"; // 실제 경로로 변경 필요
//        List<FileUpLoadEntity> entities = files.stream()
//            .filter(file -> {
//                if (file == null || file.isEmpty()) return false;
//                String ext = getExtension(file.getOriginalFilename());
//                String contentType = file.getContentType();
//
//                return ext != null && allowedExtensions.contains(ext.toLowerCase())
//                        && contentType != null && allowedMimeTypes.contains(contentType.toLowerCase())
//                        && file.getSize() <= maxSize;
//            })
//            .map(file -> {
//                String ext = getExtension(file.getOriginalFilename());
//                String savedName = UUID.randomUUID().toString() + "." + ext;
//                Path target = Paths.get(uploadDir, savedName);
//
//                try {
//                    file.transferTo(target);
//                } catch (IOException e) {
//                    throw new BbsException("파일 저장 실패: " + file.getOriginalFilename(), e);
//                }
//
//                return FileUpLoadEntity.builder()
//                    .bbs(bbs)
//                    .originalName(file.getOriginalFilename())
//                    .savedName(savedName)
//                    .path(uploadDir)
//                    .size(file.getSize())
//                    .extension(ext)
//                    .build();
//            })
//            .collect(Collectors.toList());
//
//        if (entities.isEmpty()) {
//            throw new BbsException("허용된 파일 형식만 첨부할 수 있습니다.");
//        }
//
//        return fileUploadRepository.saveAll(entities).stream()
//            .map(entity -> FileUpLoadDto.dtoBuilder()
//                .fileNum(entity.getFilenum())
//                .originalName(entity.getOriginalName())
//                .savedName(entity.getSavedName())
//                .path(entity.getPath())
//                .size(entity.getSize())
//                .extension(entity.getExtension())
//                .build())
//            .collect(Collectors.toList());
//    }
//
//    private String getExtension(String filename) {
//        if (filename == null || filename.isBlank()) return null;
//        int dotIndex = filename.lastIndexOf('.');
//        if (dotIndex == -1 || dotIndex == filename.length() - 1) return null;
//        return filename.substring(dotIndex + 1).toLowerCase();
//    }
//    @Override
//    public List<FileUpLoadDto> getFilesByBbs(Long bbsId) {
//        return fileUploadRepository.findByBbsBulletinNum(bbsId).stream()
//            .map(entity -> FileUpLoadDto.dtoBuilder()
//                .fileNum(entity.getFilenum())
//                .originalName(entity.getOriginalName())
//                .savedName(entity.getSavedName())
//                .path(entity.getPath())
//                .size(entity.getSize())
//                .extension(entity.getExtension())
//                .build())
//            .collect(Collectors.toList());
//    }
//    
//    @Override
//    @Transactional
//    public FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto, MultipartFile newFile) {
//        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
//            .orElseThrow(() -> new BbsException("파일 없음"));
//
//        // 실제 파일 저장
//        if (newFile != null && !newFile.isEmpty()) {
//            try {
//                String ext = getExtension(newFile.getOriginalFilename());
//                String savedName = UUID.randomUUID() + "." + ext;
//                String uploadDir = "C:/photo"; // 또는 네 환경에 맞는 경로
//
//                Path path = Paths.get(uploadDir, savedName);
//                newFile.transferTo(path);  // 실제 파일 저장
//
//                // 파일 메타데이터 업데이트
//                file.setOriginalName(newFile.getOriginalFilename());
//                file.setSavedName(savedName);
//                file.setPath("/uploads/" + savedName);
//                file.setSize(newFile.getSize());
//                file.setExtension(ext);
//
//            } catch (IOException e) {
//                throw new BbsException("파일 저장 실패", e);
//            }
//        }
//
//        return FileUpLoadDto.dtoBuilder()
//            .fileNum(file.getFilenum())
//            .originalName(file.getOriginalName())
//            .savedName(file.getSavedName())
//            .path(file.getPath())
//            .size(file.getSize())
//            .extension(file.getExtension())
//            .build();
//    }
//    @Transactional
//    public void deleteFileById(Long fileId) {
//        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
//            .orElseThrow(() -> new BbsException("삭제할 파일이 존재하지 않습니다: " + fileId));
//
//        // 실제 저장소 파일 삭제 로직이 있다면 추가 (예: 파일 시스템에서 삭제)
//        // 예) Files.deleteIfExists(Paths.get(file.getPath(), file.getSavedName()));
//
//        fileUploadRepository.delete(file);
//    }
//    
//    @Override
//    public Map<String, Object> getBbsList(BoardType type, int page, int size, String bbstitle, String memberName, String bbscontent) {
//        // 등록일 기준 내림차순 정렬 (최신글 맨 위)
//        Pageable pageable = PageRequest.of(page, size, Sort.by("registdate").descending());
//        
//        // Repository 호출
//        Page<BbsDto> pageResult = bbsRepository.findBbsByTypeAndSearch(type, bbstitle, memberName, bbscontent, pageable);
//        List<BbsDto> list = pageResult.getContent(); // Page -> List
//        long total = pageResult.getTotalElements();  // 전체 개수
//        
//        Map<String, Object> result = new HashMap<>();
//        result.put("list", list);
//        result.put("total", total);
//        result.put("page", page);
//        result.put("size", size);
//        return result;
//    }
//
//
//
//
//
//}