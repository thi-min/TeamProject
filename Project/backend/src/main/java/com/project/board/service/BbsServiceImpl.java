package com.project.board.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.BbsSimpleResponseDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.dto.QandADto;
import com.project.board.entity.BbsEntity;
import com.project.board.entity.FileUpLoadEntity;
import com.project.board.entity.ImageBbsEntity;
import com.project.board.entity.QandAEntity;
import com.project.board.exception.BbsException;
import com.project.board.repository.BbsRepository;
import com.project.board.repository.FileUpLoadRepository;
import com.project.board.repository.ImageBbsRepository;
import com.project.board.repository.QandARepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BbsServiceImpl implements BbsService {

    private final BbsRepository bbsRepository;
    private final QandARepository qandARepository;
    private final ImageBbsRepository imageBbsRepository;
    private final FileUpLoadRepository fileUploadRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    // =========================
    // 📌 application.properties 값 주입 (물리 저장소 경로)
    //   - 프론트는 /DATA/... 로 접근하므로 DB에는 /DATA/... 만 저장
    //   - 실제 저장/삭제는 아래 물리 경로를 사용
    // =========================
    @Value("${file.upload-imgbbs}")
    private String imgBbsUploadDir;   // ../frontend/public/DATA/bbs/imgBbs

    @Value("${file.upload-norbbs}")
    private String norBbsUploadDir;   // ../frontend/public/DATA/bbs/norBbs

    @Value("${file.upload-quesbbs}")
    private String quesBbsUploadDir;  // ../frontend/public/DATA/bbs/quesBbs
    
    @Value("${file.upload-sumnel}")
    private String thumbnailUploadDir;   // ../frontend/public/DATA/bbs/thumbnail
    
    // =========================
    // 📌 게시판 타입별 물리 저장소 경로
    // =========================
    private String getUploadDir(BoardType boardType) {
        return switch (boardType) {
            case POTO -> imgBbsUploadDir;
            case NORMAL -> norBbsUploadDir;
            case FAQ -> quesBbsUploadDir;
            default -> throw new BbsException("지원하지 않는 게시판 타입입니다.");
        };
    }

    // =========================
    // 📌 게시판 타입별 웹 경로(/DATA/...) 생성
    // =========================
    private String getWebPath(BoardType boardType, String savedName) {
        return switch (boardType) {
            case POTO -> "/DATA/bbs/imgBbs/" + savedName;
            case NORMAL -> "/DATA/bbs/norBbs/" + savedName;
            case FAQ -> "/DATA/bbs/quesBbs/" + savedName;
            default -> throw new BbsException("지원하지 않는 게시판 타입입니다.");
        };
    }
    // ★ 썸네일의 /DATA 경로 생성
    private String getThumbnailWebPath(String savedName) {
        return "/DATA/bbs/thumbnail/" + savedName;
    }
    
    // ---------------- 게시글 저장(메타만) ----------------
    private BbsDto saveOnlyBbs(BbsDto dto, Long requesterMemberNum, String requesterAdminId) {
        MemberEntity member = null;

        // ✅ memberNum 존재 시에만 관계 설정
        if (dto.getMemberNum() != null) {
            member = memberRepository.findByMemberNum(dto.getMemberNum())
                    .orElseThrow(() -> new BbsException("회원이 존재하지 않습니다."));
        }

        BbsEntity.BbsEntityBuilder builder = BbsEntity.builder()
                .bulletinNum(dto.getBulletinNum())
                .bbstitle(dto.getBbsTitle())
                .bbscontent(dto.getBbsContent())
                .registdate(LocalDateTime.now())
                .revisiondate(dto.getRevisionDate())
                .deldate(dto.getDelDate())
                .viewers(dto.getViewers() != null ? dto.getViewers() : 0)
                .bulletinType(dto.getBulletinType());

        if (member != null) {
            builder.memberNum(member);
        }

        BbsEntity entity = builder.build();
        BbsEntity savedEntity = bbsRepository.save(entity);

        return convertToDto(savedEntity);
    }

    // ---------------- 최상위 생성 메소드 ----------------
    @Override
    @Transactional
    public BbsDto createBbs(BbsDto dto, Long requesterMemberNum, String requesterAdminId,
                            List<MultipartFile> files, List<String> insertOptions,
                            List<String> isRepresentativeList) {

        BoardType type = dto.getBulletinType();

        // 로그인/권한 체크
        if ((type == BoardType.FAQ || type == BoardType.POTO) && requesterMemberNum == null) {
            throw new BbsException("로그인 후 작성 가능합니다.");
        }
        if (type == BoardType.NORMAL && requesterAdminId == null) {
            throw new BbsException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        // DTO에 memberNum 설정
        dto.setMemberNum(requesterMemberNum);

        // 게시글 저장(첨부 제외)
        BbsDto savedDto = saveOnlyBbs(dto, requesterMemberNum, requesterAdminId);

        if (type == BoardType.POTO) {
            return createPotoBbs(savedDto, requesterMemberNum, files, isRepresentativeList);
        } else {
            // 1) 첨부 저장 + 2) 본문 삽입 처리
            BbsDto result = createBbsWithFiles(savedDto, requesterMemberNum, requesterAdminId, files, insertOptions);

            // (추가 보정) 저장 직후 DB에서 다시 조회하여 /DATA 경로 기반으로 본문에 삽입
            if (files != null && insertOptions != null) {
                StringBuilder contentBuilder = new StringBuilder(result.getBbsContent() == null ? "" : result.getBbsContent());

                List<FileUpLoadDto> savedFiles = getFilesByBbs(result.getBulletinNum());
                for (int i = 0; i < savedFiles.size(); i++) {
                    FileUpLoadDto f = savedFiles.get(i);
                    String ext = f.getExtension() != null ? f.getExtension().toLowerCase() : "";
                    String option = (insertOptions.size() > i) ? insertOptions.get(i) : "no-insert";

                    // ✅ 본문 삽입은 이미지 계열만 (jpg/jpeg/png)
                    if ("insert".equals(option) && List.of("jpg", "jpeg", "png").contains(ext)) {
                        // ✅ 프론트가 직접 읽는 /DATA/... 경로를 본문에 삽입
                        contentBuilder.append("<br><img src='")
                                      .append(f.getPath()) // /DATA/...
                                      .append("' style='max-width:600px;'/>");
                    }
                }

                // 본문 업데이트 후 저장
                result.setBbsContent(contentBuilder.toString());
                saveOnlyBbs(result, requesterMemberNum, requesterAdminId);
            }

            return result;
        }
    }

    @Override
    public FileUpLoadDto getFileById(Long fileId) {
        return bbsRepository.findFileById(fileId)
                .orElseThrow(() -> new BbsException("해당 파일이 존재하지 않습니다. ID: " + fileId));
    }

    // ---------------- POTO 게시판 처리 ----------------
    @Override
    @Transactional
    public BbsDto createPotoBbs(BbsDto dto, Long requesterMemberNum,
                                List<MultipartFile> files, List<String> isRepresentativeList) {

        if (files == null || files.isEmpty()) {
            throw new BbsException("이미지 게시판은 최소 1장 이상의 사진을 등록해야 합니다.");
        }

        MemberEntity member = memberRepository.findById(requesterMemberNum)
                .orElseThrow(() -> new BbsException("회원 정보가 존재하지 않습니다."));

        // 게시글 저장
        BbsEntity savedEntity = BbsEntity.builder()
                .bbstitle(dto.getBbsTitle())
                .bbscontent(dto.getBbsContent())
                .bulletinType(dto.getBulletinType())
                .memberNum(member)
                .registdate(LocalDateTime.now())
                .viewers(0)
                .build();
        savedEntity = bbsRepository.save(savedEntity);

        if (isRepresentativeList == null || isRepresentativeList.size() != files.size()) {
            throw new BbsException("대표 이미지 정보가 올바르지 않습니다.");
        }

        ImageBbsEntity representativeImage = null;
        List<String> allowedExtensions = List.of("jpg", "jpeg");
        List<String> allowedMimeTypes = List.of("image/jpeg");
        long maxSize = 5 * 1024 * 1024;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file == null || file.isEmpty()) continue;

            String ext = getExtension(file.getOriginalFilename());
            String contentType = file.getContentType();

            if (ext == null || !allowedExtensions.contains(ext.toLowerCase())
                    || contentType == null || !allowedMimeTypes.contains(contentType.toLowerCase())
                    || file.getSize() > maxSize) {
                throw new BbsException("첨부파일은 jpg 또는 jpeg 이미지만 가능합니다. (" + file.getOriginalFilename() + ")");
            }

            String savedName = UUID.randomUUID() + "." + ext;

            // 원본 이미지 저장
            Path imgDir = resolveAndEnsureDir(getUploadDir(BoardType.POTO));
            Path imgTarget = imgDir.resolve(savedName);
            try {
                file.transferTo(imgTarget.toFile());
            } catch (IOException e) {
                throw new BbsException("이미지 저장 실패: " + file.getOriginalFilename(), e);
            }

            // 파일 메타(첨부) 저장 — DB엔 /DATA/... 만
            FileUpLoadEntity fileEntity = FileUpLoadEntity.builder()
                    .bbs(savedEntity)
                    .originalName(file.getOriginalFilename())
                    .savedName(savedName)
                    .path(getWebPath(BoardType.POTO, savedName)) // ✅ /DATA/bbs/imgBbs/...
                    .size(file.getSize())
                    .extension(ext)
                    .build();
            fileUploadRepository.save(fileEntity);

            // ✅ 대표 이미지일 경우: 썸네일 생성 + ImageBbsEntity 저장
            if ("Y".equalsIgnoreCase(isRepresentativeList.get(i)) && representativeImage == null) {
                Path thumbDir = resolveAndEnsureDir(thumbnailUploadDir);
                Path thumbTarget = thumbDir.resolve(savedName);

                // 300x300 리사이즈 썸네일 생성
                createJpegThumbnail(imgTarget, thumbTarget, 300, 300);

                ImageBbsEntity repImg = ImageBbsEntity.builder()
                        .bbs(savedEntity)
                        .thumbnailPath(getThumbnailWebPath(savedName)) // ✅ /DATA/bbs/thumbnail/...
                        .imagePath(getWebPath(BoardType.POTO, savedName)) // ✅ /DATA/bbs/imgBbs/...
                        .build();
                representativeImage = imageBbsRepository.save(repImg);
            }
        }

        if (representativeImage == null) {
            throw new BbsException("대표 이미지를 반드시 선택해야 합니다.");
        }

        dto.setBulletinNum(savedEntity.getBulletinNum());
        return dto;
    }



    // ---------------- 일반 게시판 파일 처리 ----------------
    @Transactional
    public BbsDto createBbsWithFiles(BbsDto savedBbs, Long requesterMemberNum, String requesterAdminId,
                                     List<MultipartFile> files, List<String> insertOptions) {

        if (files != null && !files.isEmpty()) {
            // 1) 파일 저장 (물리 저장 + DB엔 /DATA)
            List<FileUpLoadDto> uploadedFiles = saveFileList(savedBbs.getBulletinNum(), files, savedBbs.getBulletinType());

            // 2) 본문 삽입 (/DATA 경로)
            String updatedContent = insertFilesToContent(savedBbs.getBbsContent(), uploadedFiles, insertOptions);

            // 3) 게시글 본문 업데이트
            BbsEntity bbsEntity = bbsRepository.findById(savedBbs.getBulletinNum())
                    .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다."));
            bbsEntity.setBbscontent(updatedContent);
            bbsRepository.save(bbsEntity);

            savedBbs.setBbsContent(updatedContent);
        }

        return savedBbs;
    }

    // ---------------- 본문 삽입 처리(이미지 계열만) ----------------
    private String insertFilesToContent(String originalContent, List<FileUpLoadDto> files, List<String> insertOptions) {
        StringBuilder content = new StringBuilder(originalContent == null ? "" : originalContent);
        List<String> imageExt = List.of("jpg", "jpeg", "png");

        for (int i = 0; i < files.size(); i++) {
            FileUpLoadDto file = files.get(i);
            String option = (insertOptions != null && insertOptions.size() > i) ? insertOptions.get(i) : "no-insert";
            String ext = file.getExtension() != null ? file.getExtension().toLowerCase() : "";
            String webUrl = file.getPath(); // ✅ /DATA/... (프론트 직접 접근)

            if ("insert".equals(option) && imageExt.contains(ext)) {
                content.append("\n<img src=\"")
                       .append(webUrl)
                       .append("\" alt=\"")
                       .append(file.getOriginalName())
                       .append("\" style='max-width:600px;' />");
            }
        }
        return content.toString();
    }

    // ---------------- 게시글 수정 ----------------
    @Override
    @Transactional(noRollbackFor = BbsException.class)
    public BbsDto updateBbs(Long id,
                            BbsDto dto,
                            Long userId,
                            String adminId,
                            List<MultipartFile> newFiles,
                            List<Long> deleteFileIds,
                            boolean isAdmin,
                            List<String> insertOptions) {

        BbsEntity bbs = bbsRepository.findById(id)
                .orElseThrow(() -> new BbsException("게시글 없음: " + id));

        // 권한 체크(관리자 또는 작성자 본인)
        if (!isAdmin && (bbs.getMemberNum() == null || !bbs.getMemberNum().getMemberNum().equals(userId))) {
            throw new BbsException("본인이 작성한 글만 수정 가능합니다.");
        }

        try {
            // 본문/제목 수정
            bbs.setBbstitle(dto.getBbsTitle());
            bbs.setBbscontent(dto.getBbsContent());
            bbs.setRevisiondate(dto.getRevisionDate());

            // 삭제 파일 처리
            if (deleteFileIds != null) {
                for (Long fileId : deleteFileIds) {
                    try {
                        deleteFileById(fileId); // 물리 + DB
                    } catch (Exception e) {
                        System.err.println("파일 삭제 실패: " + fileId + ", " + e.getMessage());
                    }
                }
            }

            // 새 파일 업로드
            if (newFiles != null && !newFiles.isEmpty()) {
                Long memberNumParam = isAdmin ? null : userId;
                String adminIdParam = isAdmin ? adminId : null;

                try {
                    this.createBbsWithFiles(convertToDto(bbs), memberNumParam, adminIdParam, newFiles, insertOptions);
                } catch (Exception e) {
                    System.err.println("파일 업로드 실패: " + e.getMessage());
                }
            }

            return convertToDto(bbsRepository.save(bbs));

        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패", e);
        }
    }

    // ---------------- 게시글 단일 삭제 ----------------
    @Override
    @Transactional
    public void deleteBbs(Long id, Long requesterMemberNum, String requesterAdminId) {
        BbsEntity bbs = bbsRepository.findById(id).orElseThrow(() -> new BbsException("게시글 없음: " + id));

        boolean isAdmin = requesterAdminId != null;
        boolean isAuthor = requesterMemberNum != null && bbs.getMemberNum() != null && requesterMemberNum.equals(bbs.getMemberNum().getMemberNum());

        if (!(isAdmin || isAuthor)) throw new BbsException("삭제 권한이 없습니다.");

        if (bbs.getBulletinType() == BoardType.FAQ) qandARepository.deleteByBbsBulletinNum(id);
        deleteFilesAndImages(bbs);
        bbsRepository.deleteById(id);
    }

    // ---------------- 다중 삭제 ----------------
    @Override
    @Transactional
    public void deleteBbsMultiple(List<Long> ids, Long requesterMemberNum, String requesterAdminId) {
        if (requesterAdminId == null) throw new BbsException("관리자 권한이 필요합니다.");

        for (Long id : ids) {
            BbsEntity bbs = bbsRepository.findById(id).orElseThrow(() -> new BbsException("게시글 없음: " + id));
            if (bbs.getBulletinType() == BoardType.FAQ) qandARepository.deleteByBbsBulletinNum(id);
            deleteFilesAndImages(bbs);
            bbsRepository.deleteById(id);
        }
    }

    // ---------------- 첨부파일 + POTO 이미지 삭제 공통 ----------------
    private void deleteFilesAndImages(BbsEntity bbs) {
        // 첨부파일(일반/FAQ/POTO 공통) — savedName 기준으로 물리 파일 삭제
        List<FileUpLoadEntity> files = fileUploadRepository.findByBbsBulletinNum(bbs.getBulletinNum());
        for (FileUpLoadEntity file : files) {
            try {
                String uploadDir = getUploadDir(bbs.getBulletinType());
                Files.deleteIfExists(Paths.get(uploadDir, file.getSavedName()));
            } catch (IOException ignored) {}
        }
        fileUploadRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());

        // POTO 대표 이미지 엔티티 정리(물리 파일은 위에서 이미 제거됨)
        if (bbs.getBulletinType() == BoardType.POTO) {
            List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bbs.getBulletinNum());
            for (ImageBbsEntity image : images) {
                try {
                    // 썸네일/원본 경로에서 파일명만 추출 후 물리 삭제 시도(중복 호출되어도 안전)
                    if (image.getImagePath() != null) {
                        String imgName = Paths.get(image.getImagePath()).getFileName().toString();
                        String uploadDir = getUploadDir(BoardType.POTO);
                        Files.deleteIfExists(Paths.get(uploadDir, imgName));
                    }
                    // ★ 썸네일 삭제
                    if (image.getThumbnailPath() != null) {
                        String thumbName = Paths.get(image.getThumbnailPath()).getFileName().toString();
                        Path thumbDir = resolveAndEnsureDir(thumbnailUploadDir);
                        Files.deleteIfExists(thumbDir.resolve(thumbName));
                    }
                    if (image.getImagePath() != null) {
                        String uploadDir = getUploadDir(BoardType.POTO);
                        String imgFileName = Paths.get(image.getImagePath()).getFileName().toString();
                        Files.deleteIfExists(Paths.get(uploadDir, imgFileName));
                    }
                } catch (IOException ignored) {}
            }
            imageBbsRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());
        }
    }

    // ---------------- 게시글 단건 조회 ----------------
    @Override
    public BbsDto getBbs(Long id) {
        BbsEntity entity = bbsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음: " + id));
        return convertToDto(entity);
    }

    @Override
    public List<BbsDto> getAllByType(BoardType type) {
        return bbsRepository.findByBulletinType(type).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable) {
        Sort sorted = "views".equals(sort) ? Sort.by("viewers").descending() : Sort.by("registdate").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorted);
        Page<BbsEntity> page = (type != null)
                ? bbsRepository.findByBulletinType(type, sortedPageable)
                : bbsRepository.findAll(sortedPageable);
        return page.map(this::convertToDto);
    }

    @Override
    public Page<BbsDto> searchPosts(String searchType, String bbstitle, String bbscontent,
                                    String memberName, BoardType type, Pageable pageable) {

        // ✅ 1) 기본 정렬 보정: 정렬이 비어 있으면 registdate DESC로 강제
        Pageable sortedPageable = pageable;
        if (pageable == null) {
            // 페이지 정보 자체가 없으면 0페이지, 10개, 최신순
            sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "registdate"));
        } else if (pageable.getSort().isUnsorted()) {
            // 정렬 지정이 없으면 최신순으로 대체
            sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "registdate")
            );
        }

        Page<BbsEntity> result;
        String typeLower = (searchType == null || searchType.isEmpty()) ? "all" : searchType.toLowerCase();

        if (type != null) {
            result = switch (typeLower) {
                case "title"   -> bbsRepository.findByBulletinTypeAndBbstitleContaining(type, bbstitle, sortedPageable);
                case "content" -> bbsRepository.findByBulletinTypeAndBbscontentContaining(type, bbscontent, sortedPageable);
                case "all"     -> bbsRepository.findByBulletinType(type, sortedPageable);
                default        -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        } else {
            result = switch (typeLower) {
                case "title"   -> bbsRepository.findByBbstitleContaining(bbstitle, sortedPageable);
                case "content" -> bbsRepository.findByBbscontentContaining(bbscontent, sortedPageable);
                case "all"     -> bbsRepository.findAll(sortedPageable);
                default        -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        }

        return result.map(this::convertToDto);
    }
    private BbsDto convertToDto(BbsEntity e) {
        String filteredName;
        if (e.getMemberNum() != null) {
            filteredName = filterName(e.getMemberNum().getMemberName());
        } else if (e.getAdminId() != null) {
            filteredName = e.getAdminId().getAdminName();
        } else {
            filteredName = "알 수 없음";
        }

        return BbsDto.builder()
                .bulletinNum(e.getBulletinNum())
                .bbsTitle(e.getBbstitle())
                .bbsContent(e.getBbscontent())
                .registDate(e.getRegistdate())
                .revisionDate(e.getRevisiondate())
                .delDate(e.getDeldate())
                .viewers(e.getViewers())
                .bulletinType(e.getBulletinType())
                .adminId(e.getAdminId() != null ? e.getAdminId().getAdminId() : null)
                .memberNum(e.getMemberNum() != null ? e.getMemberNum().getMemberNum() : null)
                .memberName(filteredName)
                .build();
    }

    private String filterName(String name) {
        if (name == null || name.length() < 2) return name;
        int len = name.length();
        if (len == 2) return name.charAt(0) + "*";
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < len - 1; i++) sb.append("*");
        sb.append(name.charAt(len - 1));
        return sb.toString();
    }

    @Transactional
    @Override
    public QandADto saveQna(Long bbsId, QandADto dto, String requesterAdminId) {
        if (requesterAdminId == null) {
            throw new BbsException("QnA 답변은 관리자만 작성할 수 있습니다.");
        }

        BbsEntity bbs = bbsRepository.findById(bbsId)
                .orElseThrow(() -> new BbsException("게시글 없음"));

        AdminEntity adminEntity = adminRepository.findFirstByAdminId(requesterAdminId)
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        Optional<QandAEntity> existingAnswer = qandARepository.findByBbsBulletinNum(bbsId);

        QandAEntity entity;
        if (existingAnswer.isPresent()) {
            entity = existingAnswer.get();
            entity.setAnswer(dto.getAnswer());
            entity.setQuestion(dto.getQuestion() != null ? dto.getQuestion() : bbs.getBbscontent());
        } else {
            entity = QandAEntity.builder()
                    .bbs(bbs)
                    .question(bbs.getBbscontent())
                    .answer(dto.getAnswer())
                    .build();
        }

        QandAEntity saved = qandARepository.save(entity);

        return QandADto.builder()
                .bulletinNum(saved.getBbs().getBulletinNum())
                .question(bbs.getBbscontent())
                .answer(saved.getAnswer())
                .build();
    }

    @Override
    public QandADto getQna(Long bbsId) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
                .orElseThrow(() -> new BbsException("게시글 없음: " + bbsId));

        return qandARepository.findByBbsBulletinNum(bbsId)
                .map(qna -> QandADto.builder()
                        .bulletinNum(bbs.getBulletinNum())
                        .question(bbs.getBbscontent())
                        .answer(qna.getAnswer())
                        .build())
                .orElse(QandADto.builder()
                        .bulletinNum(bbs.getBulletinNum())
                        .question(bbs.getBbscontent())
                        .answer("")
                        .build());
    }

    @Override
    public void deleteQna(Long qnaId, Long adminId) {
        QandAEntity qna = qandARepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA 답변이 존재하지 않습니다."));
        if (adminId == null) {
            throw new IllegalArgumentException("관리자 ID가 필요합니다.");
        }
        qandARepository.delete(qna);
    }

    @Override
    public QandADto updateQna(Long qnaId, QandADto dto) {
        QandAEntity qna = qandARepository.findById(qnaId)
                .orElseThrow(() -> new BbsException("QnA 없음"));

        qna.setAnswer(dto.getAnswer());
        qna.setQuestion(dto.getQuestion() != null ? dto.getQuestion() : qna.getBbs().getBbscontent());

        return QandADto.builder()
                .bulletinNum(qna.getBbs().getBulletinNum())
                .question(qna.getBbs().getBbscontent())
                .answer(qna.getAnswer())
                .build();
    }

    @Override
    public List<ImageBbsDto> getImageBbsList(Long bbsId) {
        return imageBbsRepository.findByBbsBulletinNum(bbsId).stream()
                .map(entity -> ImageBbsDto.builder()
                        .bulletinNum(entity.getBbs().getBulletinNum())
                        .thumbnailPath(entity.getThumbnailPath()) // /DATA/...
                        .imagePath(entity.getImagePath())         // /DATA/...
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(noRollbackFor = BbsException.class)
    public BbsDto updatePotoBbs(Long bulletinNum,
                                BbsDto dto,
                                List<MultipartFile> newFiles,
                                List<Long> representativeFileIds,
                                List<Long> deletedFileIds,
                                List<Long> overwriteFileIds,
                                Long requesterMemberNum) {

        // 1) 게시글 조회
        BbsEntity bbs = bbsRepository.findById(bulletinNum)
                .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다."));

        // ✅ 관리자 호출이면 memberNum 업데이트 안 함
        if (requesterMemberNum != null) {
            MemberEntity member = memberRepository.findById(requesterMemberNum)
                    .orElseThrow(() -> new BbsException("회원 정보가 존재하지 않습니다."));
            bbs.setMemberNum(member);
        }

        // 2) 제목/내용 수정
        bbs.setBbstitle(dto.getBbsTitle());
        bbs.setBbscontent(dto.getBbsContent());
        bbs.setRegistdate(LocalDateTime.now());
        bbsRepository.save(bbs);
        
        // 3) 삭제 처리
        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            for (Long fileId : deletedFileIds) {
                fileUploadRepository.findById(fileId).ifPresent(fileEntity -> {
                    // 대표 이미지가 이 파일을 참조 중이면 엔티티 제거
                    imageBbsRepository.findByBbsBulletinNum(bbs.getBulletinNum())
                            .stream()
                            .filter(img -> img.getImagePath() != null && img.getImagePath().endsWith(fileEntity.getSavedName()))
                            .forEach(imageBbsRepository::delete);

                    try {
                        String uploadDir = getUploadDir(BoardType.POTO);
                        Files.deleteIfExists(Paths.get(uploadDir, fileEntity.getSavedName()));
                        
                    	// ★ 썸네일도 삭제 (있을 수도 있고 없을 수도 있음)
                        Path thumbDir = resolveAndEnsureDir(thumbnailUploadDir);
                        Files.deleteIfExists(thumbDir.resolve(fileEntity.getSavedName()));
                        
                    } catch (IOException e) {
                        throw new BbsException("파일 삭제 실패: " + fileEntity.getOriginalName());
                    }
                    fileUploadRepository.delete(fileEntity);
                });
            }
        }

        // 4) 기존 파일 목록 확보
        List<FileUpLoadEntity> existingFiles = fileUploadRepository.findByBbsBulletinNum(bbs.getBulletinNum());
        List<FileUpLoadEntity> combinedFiles = new ArrayList<>(existingFiles);

        // 덮어쓰기 처리
        if (overwriteFileIds != null) {
            for (Long overwriteId : overwriteFileIds) {
                fileUploadRepository.findById(overwriteId).ifPresent(oldFile -> {
                    try {
                        String uploadDir = getUploadDir(BoardType.POTO);
                        Files.deleteIfExists(Paths.get(uploadDir, oldFile.getSavedName()));
                    } catch (IOException e) {
                        throw new BbsException("파일 삭제 실패: " + oldFile.getOriginalName());
                    }
                    fileUploadRepository.delete(oldFile);
                    combinedFiles.remove(oldFile);
                });
            }
        }

        // 5) 새 파일 저장
        List<FileUpLoadEntity> newFileEntities = new ArrayList<>();

        if (newFiles != null) {
            for (MultipartFile file : newFiles) {
                if (file == null || file.isEmpty()) continue;

                String ext = getExtension(file.getOriginalFilename());
                if (ext == null || !List.of("jpg", "jpeg").contains(ext.toLowerCase()) || file.getSize() > 5 * 1024 * 1024) {
                    throw new BbsException("첨부파일은 jpg/jpeg만 가능: " + file.getOriginalFilename());
                }

                String savedName = UUID.randomUUID() + "." + ext;

                // ✅ 업로드 경로 보정 (물리 저장소 확보)
                Path imgDir = resolveAndEnsureDir(getUploadDir(BoardType.POTO));
                Path imgTarget = imgDir.resolve(savedName);

                try {
                    // 파일 저장
                    file.transferTo(imgTarget.toFile());
                } catch (IOException e) {
                    throw new BbsException("파일 저장 실패: " + file.getOriginalFilename(), e);
                }

                // DB 메타 저장 (웹 접근 경로는 /DATA/...)
                FileUpLoadEntity newFileEntity = FileUpLoadEntity.builder()
                        .bbs(bbs)
                        .originalName(file.getOriginalFilename())
                        .savedName(savedName)
                        .path(getWebPath(BoardType.POTO, savedName)) // ✅ /DATA/... 저장
                        .size(file.getSize())
                        .extension(ext)
                        .build();

                fileUploadRepository.save(newFileEntity);
                combinedFiles.add(newFileEntity);
                newFileEntities.add(newFileEntity);
            }
        }


     // 6) 대표 이미지 처리
        if (representativeFileIds == null || representativeFileIds.isEmpty()) {
            // 기존 대표 이미지가 있으면 그대로 유지
            ImageBbsEntity existingRep = imageBbsRepository.findByBbsBulletinNum(bbs.getBulletinNum())
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (existingRep != null) {
                dto.setBulletinNum(bbs.getBulletinNum());
                return dto; // ✅ 대표 이미지 변경 없음, 그대로 리턴
            }

            throw new BbsException("대표 이미지는 반드시 1장 선택해야 합니다.");
        }

        Long repId = representativeFileIds.get(0);

        // 기존 파일 또는 새 파일에서 찾기
        FileUpLoadEntity repFile = combinedFiles.stream()
                .filter(f -> f.getFilenum() != null && f.getFilenum().equals(repId))
                .findFirst()
                .orElseGet(() -> newFileEntities.stream()
                        .filter(f -> f.getFilenum() != null && f.getFilenum().equals(repId))
                        .findFirst()
                        .orElseThrow(() -> new BbsException("대표 이미지 파일이 존재하지 않습니다."))
                );

        // 기존 대표 이미지 삭제 후 교체
        imageBbsRepository.findByBbsBulletinNum(bbs.getBulletinNum())
                .forEach(imageBbsRepository::delete);

        // 1) 썸네일 300x300 보장 생성
        Path imgDir = resolveAndEnsureDir(getUploadDir(BoardType.POTO));
        Path imgSrc = imgDir.resolve(repFile.getSavedName());

        Path thumbDir = resolveAndEnsureDir(thumbnailUploadDir);
        Path thumbTarget = thumbDir.resolve(repFile.getSavedName());

        // 썸네일이 없으면 생성 (있어도 재생성 원하면 Files.exists 체크없이 생성)
        if (Files.notExists(thumbTarget)) {
            createJpegThumbnail(imgSrc, thumbTarget, 300, 300);
        }

        // 2) 올바른 경로로 저장 (thumbnailPath → /DATA/bbs/thumbnail/..., imagePath → /DATA/bbs/imgBbs/...)
        ImageBbsEntity repImg = ImageBbsEntity.builder()
                .bbs(bbs)
                .thumbnailPath(getThumbnailWebPath(repFile.getSavedName()))   // ✅ /DATA/bbs/thumbnail/...
                .imagePath(getWebPath(BoardType.POTO, repFile.getSavedName()))// ✅ /DATA/bbs/imgBbs/...
                .build();

        imageBbsRepository.save(repImg);

        dto.setBulletinNum(bbs.getBulletinNum());
        return dto;
    }

    // ---------------- 대표 이미지 조회 ----------------
    @Override
    public ImageBbsDto getRepresentativeImage(Long bulletinNum) {
        List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bulletinNum);
        if (images.isEmpty()) return null;

        ImageBbsEntity representativeImage = images.get(0);

        return ImageBbsDto.builder()
                .bulletinNum(bulletinNum)
                .thumbnailPath(representativeImage.getThumbnailPath()) // /DATA/...
                .imagePath(representativeImage.getImagePath())         // /DATA/...
                .build();
    }

    private String getFileNameFromPath(String path) {
        if (path == null) return null;
        return Paths.get(path).getFileName().toString();
    }

 // =========================
 // 📌 파일 저장 로직 (일반/FAQ/포토 공용)
 //   - 실제 파일은 물리 경로에 저장
 //   - DB에는 /DATA/... 만 저장
 //   - 확장자/MIME/용량 검증 포함
 // =========================
 @Override
 public List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType) {
     BbsEntity bbs = bbsRepository.findById(bbsId)
             .orElseThrow(() -> new BbsException("해당 게시글이 존재하지 않습니다"));

     // ✅ 절대경로로 보정 + 디렉터리 생성
     String uploadDir = getUploadDir(boardType); // application.yml 값
     File uploadPath = new File(uploadDir);
     if (!uploadPath.exists()) {
         boolean created = uploadPath.mkdirs();
         if (!created) {
             throw new BbsException("업로드 경로 생성 실패: " + uploadDir);
         }
     }

     // ✅ 게시판 타입별 허용 확장자/MIME/사이즈
     List<String> allowedExt;
     List<String> allowedMime;
     long maxSize = 5 * 1024 * 1024; // 5MB

     switch (boardType) {
         case POTO -> {
             allowedExt  = List.of("jpg", "jpeg");
             allowedMime = List.of("image/jpeg");
         }
         case NORMAL, FAQ -> {
             allowedExt  = List.of("jpg", "jpeg", "png", "pdf", "ppt", "pptx", "doc", "docx");
             allowedMime = List.of(
                     "image/jpeg", "image/png",
                     "application/pdf",
                     "application/vnd.ms-powerpoint",
                     "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                     "application/msword",
                     "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
             );
         }
         default -> throw new BbsException("지원하지 않는 게시판 타입입니다.");
     }

     List<FileUpLoadEntity> entities = new ArrayList<>();

     for (MultipartFile file : files) {
         if (file == null || file.isEmpty()) continue;

         // ✅ 검증
         String ext  = getExtension(file.getOriginalFilename());
         String mime = file.getContentType();
         long size   = file.getSize();

         if (ext == null || !allowedExt.contains(ext.toLowerCase())
                 || mime == null || !allowedMime.contains(mime.toLowerCase())
                 || size > maxSize) {
             throw new BbsException("허용되지 않은 파일 형식/크기입니다: " + file.getOriginalFilename());
         }

         // 저장 파일명
         String savedName = UUID.randomUUID() + "." + ext;
         try {
             Path target = Paths.get(uploadPath.getAbsolutePath(), savedName);
             file.transferTo(target.toFile());
         } catch (IOException e) {
             throw new BbsException("파일 저장 실패: " + file.getOriginalFilename(), e);
         }

         // DB 메타 — /DATA/...
         FileUpLoadEntity entity = FileUpLoadEntity.builder()
                 .bbs(bbs)
                 .originalName(file.getOriginalFilename())
                 .savedName(savedName)
                 .path(getWebPath(boardType, savedName)) // "/DATA/bbs/..." 경로
                 .size(size)
                 .extension(ext)
                 .build();
         entities.add(entity);
     }

     return fileUploadRepository.saveAll(entities).stream()
             .map(e -> FileUpLoadDto.dtoBuilder()
                     .fileNum(e.getFilenum())
                     .originalName(e.getOriginalName())
                     .savedName(e.getSavedName())
                     .path(e.getPath())     // /DATA/...
                     .size(e.getSize())
                     .extension(e.getExtension())
                     .build())
             .collect(Collectors.toList());
 }


    private String getExtension(String filename) {
        if (filename == null || filename.isBlank()) return null;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) return null;
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    @Override
    public List<FileUpLoadDto> getFilesByBbs(Long bbsId) {
        return fileUploadRepository.findByBbsBulletinNum(bbsId).stream()
                .map(entity -> FileUpLoadDto.dtoBuilder()
                        .fileNum(entity.getFilenum())
                        .originalName(entity.getOriginalName())
                        .savedName(entity.getSavedName())
                        .path(entity.getPath()) // /DATA/...
                        .size(entity.getSize())
                        .extension(entity.getExtension())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto, MultipartFile newFile) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new BbsException("파일 없음"));

        if (newFile != null && !newFile.isEmpty()) {
            try {
                // ✅ 게시판 타입별 검증 재적용
                BoardType type = file.getBbs().getBulletinType();
                List<String> allowedExt;
                List<String> allowedMime;
                long maxSize = 5 * 1024 * 1024;

                switch (type) {
                    case POTO -> {
                        allowedExt  = List.of("jpg", "jpeg");
                        allowedMime = List.of("image/jpeg");
                    }
                    case NORMAL, FAQ -> {
                        allowedExt  = List.of("jpg", "jpeg", "png", "pdf", "ppt", "pptx", "doc", "docx");
                        allowedMime = List.of(
                                "image/jpeg", "image/png",
                                "application/pdf",
                                "application/vnd.ms-powerpoint",
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        );
                    }
                    default -> throw new BbsException("지원하지 않는 게시판 타입입니다.");
                }

                String ext  = getExtension(newFile.getOriginalFilename());
                String mime = newFile.getContentType();
                long size   = newFile.getSize();
                if (ext == null || !allowedExt.contains(ext.toLowerCase())
                        || mime == null || !allowedMime.contains(mime.toLowerCase())
                        || size > maxSize) {
                    throw new BbsException("허용되지 않은 파일 형식/크기입니다: " + newFile.getOriginalFilename());
                }

                // 기존 물리 파일 삭제
                try {
                    String oldSavedName = file.getSavedName();
                    if (oldSavedName != null && !oldSavedName.isBlank()) {
                        String oldUploadDir = getUploadDir(type);
                        Files.deleteIfExists(Paths.get(oldUploadDir, oldSavedName));
                    }
                } catch (IOException ignore) {}

                // 새 파일 저장
                String savedName = UUID.randomUUID() + "." + ext;
                String uploadDir = getUploadDir(type);
                Path path = Paths.get(uploadDir, savedName);
                newFile.transferTo(path.toFile());

                // ✅ DB 메타 갱신 — /DATA/...
                file.setOriginalName(newFile.getOriginalFilename());
                file.setSavedName(savedName);
                file.setPath(getWebPath(type, savedName)); // ★ 기존 "/uploads/..." → "/DATA/..."로 수정
                file.setSize(size);
                file.setExtension(ext);

            } catch (IOException e) {
                throw new BbsException("파일 저장 실패", e);
            }
        }

        return FileUpLoadDto.dtoBuilder()
                .fileNum(file.getFilenum())
                .originalName(file.getOriginalName())
                .savedName(file.getSavedName())
                .path(file.getPath()) // /DATA/...
                .size(file.getSize())
                .extension(file.getExtension())
                .build();
    }

    @Transactional
    public void deleteFileById(Long fileId) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new BbsException("삭제할 파일이 존재하지 않습니다: " + fileId));

        // 물리 파일 삭제
        try {
            String uploadDir = getUploadDir(file.getBbs().getBulletinType());
            Files.deleteIfExists(Paths.get(uploadDir, file.getSavedName()));
        } catch (IOException ignore) {}

        // 메타 삭제
        fileUploadRepository.delete(file);
    }

    @Override
    public Map<String, Object> getBbsList(BoardType type, int page, int size, String bbstitle, String memberName, String bbscontent) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("registdate").descending());
        Page<BbsDto> pageResult = bbsRepository.findBbsByTypeAndSearch(type, bbstitle, memberName, bbscontent, pageable);
        List<BbsDto> list = pageResult.getContent();
        long total = pageResult.getTotalElements();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    @Override
    public List<BbsSimpleResponseDto> getLatestNormalPosts() {
        return bbsRepository.findTop5ByBulletinTypeOrderByRegistdateDesc(BoardType.NORMAL)
                .stream()
                .map(b -> BbsSimpleResponseDto.builder()
                        .bulletinNum(b.getBulletinNum())
                        .bbstitle(b.getBbstitle())
                        .registdate(b.getRegistdate().toLocalDate())
                        .build())
                .toList();
    }
    
    // ===== 업로드 경로 보정 & 디렉터리 생성 유틸 =====
    /**
     * application.properties에서 받은 경로(상대/절대 무관)를
     * 절대경로로 정규화한 뒤, 존재하지 않으면 디렉터리를 생성합니다.
     * 예) ../frontend/public/DATA/bbs/imgBbs  →  C:\...\frontend\public\DATA\bbs\imgBbs
     */
    private Path resolveAndEnsureDir(String dir) {
    	Path base = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            throw new BbsException("업로드 디렉터리 생성 실패: " + base, e);
        }
        return base;
    }
    
    /**
     * JPEG 썸네일 생성 (지정 크기로 리사이즈)
     * @param source 원본 이미지 경로
     * @param target 썸네일 저장 경로
     * @param width  썸네일 가로 크기
     * @param height 썸네일 세로 크기
     */
    private void createJpegThumbnail(Path source, Path target, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(source.toFile());
            if (originalImage == null) {
                throw new IOException("이미지를 읽을 수 없습니다: " + source);
            }

            // 리사이즈
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            // 디렉토리 생성 보장
            Files.createDirectories(target.getParent());

            // JPEG로 저장
            try (OutputStream os = Files.newOutputStream(target)) {
                ImageIO.write(resizedImage, "jpg", os);
            }

        } catch (IOException e) {
            throw new BbsException("썸네일 생성 실패: " + source, e);
        }
    }


}
