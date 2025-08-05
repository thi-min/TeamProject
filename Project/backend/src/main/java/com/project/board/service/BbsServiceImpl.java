package com.project.board.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
 
    
 // 게시글 저장만 담당하는 메소드
    private BbsDto saveOnlyBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId) {
        MemberEntity member = null;
        if(dto.getMemberNum() != null) {
            member = memberRepository.findByMemberNum(dto.getMemberNum())
                    .orElseThrow(() -> new BbsException("회원이 존재하지 않습니다."));
        }

        BbsEntity.BbsEntityBuilder builder = BbsEntity.builder()
                .bulletinNum(dto.getBulletinNum())
                .bbstitle(dto.getBbsTitle())
                .bbscontent(dto.getBbsContent())
                .registdate(dto.getRegistDate())
                .revisiondate(dto.getRevisionDate())
                .deldate(dto.getDelDate())
                .viewers(dto.getViewers())
                .bulletinType(dto.getBulletinType());

        if (member != null) {
            builder.memberNum(member);
        }

        BbsEntity entity = builder.build();
        BbsEntity savedEntity = bbsRepository.save(entity);

        return convertToDto(savedEntity);
    }

    // 최상위 호출 메소드: 게시글 저장 후 분기 처리
    @Override
    public BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId, List<MultipartFile> files) {
        BoardType type = dto.getBulletinType();

        if (type == BoardType.NORMAL && requesterAdminId == null) {
            throw new BbsException("공지사항은 관리자만 작성할 수 있습니다.");
        }
        if ((type == BoardType.FAQ || type == BoardType.POTO) && requesterMemberNum == null) {
            throw new BbsException("해당 게시판은 회원만 작성할 수 있습니다.");
        }

        // 게시글 저장만 처리
        BbsDto savedDto = saveOnlyBbs(dto, requesterMemberNum, requesterAdminId);

        // 타입에 따른 후속 작업 분기
        if (type == BoardType.POTO) {
            return createPotoBbs(savedDto, requesterMemberNum, files);  // 여기서 다시 createBbs 호출 NO!
        } else {
            return createBbsWithFiles(savedDto, requesterMemberNum, requesterAdminId, files);
        }
    }

 // 대표 이미지 1개 + 첨부파일 저장
    @Override
    @Transactional
    public BbsDto createPotoBbs(BbsDto savedBbs, Long requesterMemberNum, List<MultipartFile> files) {
        BoardType type = savedBbs.getBulletinType();

        if (type != BoardType.POTO) {
            throw new BbsException("createPotoBbs는 POTO 타입 게시글만 처리합니다.");
        }

        if (files == null || files.isEmpty()) {
            throw new BbsException("이미지 게시판은 최소 1장 이상의 사진을 등록해야 합니다.");
        }

        // 이미지 저장 조건
        List<String> allowedExtensions = List.of("jpg", "jpeg");
        List<String> allowedMimeTypes = List.of("image/jpeg");
        long maxSize = 5 * 1024 * 1024;
        String uploadDir = "C:/Image";

        ImageBbsEntity imageBbs = null;

        for (MultipartFile file : files) {
            String ext = getExtension(file.getOriginalFilename());
            String contentType = file.getContentType();

            if (file == null || file.isEmpty()
                    || ext == null || !allowedExtensions.contains(ext.toLowerCase())
                    || contentType == null || !allowedMimeTypes.contains(contentType.toLowerCase())
                    || file.getSize() > maxSize) {
                continue;
            }

            String savedName = UUID.randomUUID().toString() + "." + ext;
            Path target = Paths.get(uploadDir, savedName);

            try {
                file.transferTo(target);
            } catch (IOException e) {
                throw new BbsException("이미지 저장 실패: " + file.getOriginalFilename(), e);
            }

            imageBbs = ImageBbsEntity.builder()
                    .bbs(bbsRepository.findById(savedBbs.getBulletinNum())
                            .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다.")))
                    .thumbnailPath("/uploads/thumb/" + savedName)
                    .imagePath("/uploads/" + savedName)
                    .build();

            break; // 대표 이미지 1개만 저장
        }

        if (imageBbs == null) {
            throw new BbsException("유효한 이미지 파일(jpg, jpeg)만 첨부할 수 있습니다.");
        }

        imageBbsRepository.save(imageBbs);

        // 모든 파일 첨부 저장
        saveFileList(savedBbs.getBulletinNum(), files, BoardType.POTO);

        return savedBbs;
    }

    @Transactional
    public BbsDto createBbsWithFiles(BbsDto savedBbs, Long requesterMemberNum, Long requesterAdminId, List<MultipartFile> files) {

        BoardType type = savedBbs.getBulletinType();

        if (files != null && !files.isEmpty()) {
            switch (type) {
                case FAQ:
                case NORMAL:
                    List<FileUpLoadDto> uploadedFiles = saveFileList(savedBbs.getBulletinNum(), files, type);
                    String updatedContent = insertFilesToContent(savedBbs.getBbsContent(), uploadedFiles);

                    BbsEntity bbsEntity = bbsRepository.findById(savedBbs.getBulletinNum())
                        .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다."));
                    bbsEntity.setBbscontent(updatedContent);
                    bbsRepository.save(bbsEntity);

                    savedBbs.setBbsContent(updatedContent);
                    break;

                default:
                    throw new BbsException("지원하지 않는 게시판 타입입니다.");
            }
        }

        return savedBbs;
    }


    private String insertFilesToContent(String originalContent, List<FileUpLoadDto> files) {
        StringBuilder contentBuilder = new StringBuilder(originalContent == null ? "" : originalContent);
        // 허용된 이미지 확장자 (본문에 <img> 태그로 삽입)
        List<String> imageExtensions = List.of("jpg", "jpeg");

        for (FileUpLoadDto file : files) {
            String extension = file.getExtension().toLowerCase();
            String fileUrl = "/uploads/" + file.getSavedName(); // 실제 경로에 맞게 수정

            if (imageExtensions.contains(extension)) {
                // 이미지 파일은 <img> 태그로 삽입
                contentBuilder.append("\n<img src=\"")
                              .append(fileUrl)
                              .append("\" alt=\"")
                              .append(file.getOriginalName())
                              .append("\" />");
            } else if (List.of("pdf", "ppt", "pptx", "doc", "docx").contains(extension)) {
                // 문서 파일은 다운로드 링크로 삽입
                contentBuilder.append("\n<a href=\"")
                              .append(fileUrl)
                              .append("\" download>")
                              .append(file.getOriginalName())
                              .append("</a>");
            } else {
                // 허용되지 않은 확장자는 무시하거나 로그 남기기
                // 예: contentBuilder.append("\n<!-- 허용되지 않은 확장자: ").append(extension).append(" -->");
            }
        }
        return contentBuilder.toString();
    }

    @Transactional
    @Override
    public BbsDto updateBbs(Long id, BbsDto dto, Long memberNum) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));
        if (!bbs.getMemberNum().getMemberNum().equals(memberNum)) {
            throw new BbsException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        bbs.setBbstitle(dto.getBbsTitle());
        bbs.setBbscontent(dto.getBbsContent());
        bbs.setRevisiondate(dto.getRevisionDate());
        return convertToDto(bbsRepository.save(bbs));
    }

    @Override
    @Transactional
    public void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));

        if (bbs.getMemberNum() != null) {
            memberRepository.findById(bbs.getMemberNum().getMemberNum())
                .orElseThrow(() -> new BbsException("작성자 회원 정보가 존재하지 않습니다."));
        }

        boolean isAdmin = requesterAdminId != null;
        boolean isAuthor = requesterMemberNum != null &&
            bbs.getMemberNum() != null &&
            requesterMemberNum.equals(bbs.getMemberNum().getMemberNum());

        if (!(isAdmin || isAuthor)) {
            throw new BbsException("삭제 권한이 없습니다.");
        }

        String uploadDir = "C:/photo"; // 실제 파일 저장 경로

        // 1. QANDA 게시판의 경우 qanda 테이블 먼저 삭제 (외래키 제약 회피)
        if (bbs.getBulletinType() == BoardType.FAQ) {
        	qandARepository.deleteByBbsBulletinNum(bbs.getBulletinNum());  // bulletin_num을 참조하는 QANDA 먼저 삭제
        }

        // 2. 첨부파일 삭제 (NORMAL, FAQ, POTO 모두 포함됨)
        switch (bbs.getBulletinType()) {
            case NORMAL:
            case FAQ:
            case POTO:
                List<FileUpLoadEntity> files = fileUploadRepository.findByBbsBulletinNum(id);
                for (FileUpLoadEntity file : files) {
                    try {
                        Path filePath = Paths.get(uploadDir, file.getSavedName());
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        System.err.println("첨부파일 삭제 실패: " + e.getMessage());
                    }
                }
                fileUploadRepository.deleteByBbsBulletinNum(id);
                break;
        }

        //  3. POTO 게시판인 경우, 대표 이미지 + 썸네일 삭제
        if (bbs.getBulletinType() == BoardType.POTO) {
            List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(id);
            for (ImageBbsEntity image : images) {
                try {
                    if (image.getThumbnailPath() != null) {
                        String thumbnailFile = Paths.get(image.getThumbnailPath()).getFileName().toString();
                        Files.deleteIfExists(Paths.get(uploadDir, thumbnailFile));
                    }
                    if (image.getImagePath() != null) {
                        String imageFile = Paths.get(image.getImagePath()).getFileName().toString();
                        Files.deleteIfExists(Paths.get(uploadDir, imageFile));
                    }
                } catch (IOException e) {
                    System.err.println("이미지 삭제 실패: " + e.getMessage());
                }
            }
            imageBbsRepository.deleteByBbsBulletinNum(id);
        }

        // ✅ 4. 마지막으로 게시글 삭제
        bbsRepository.deleteById(id);
    }



    @Override
    public void deleteBbs(List<Long> ids, Long requesterAdminId, Long requesterMemberNum) {
        for (Long id : ids) {
            deleteBbs(id, requesterMemberNum, requesterAdminId);
        }
    }

    @Override
    public BbsDto getBbs(Long id) {
        return bbsRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));
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
    public Page<BbsDto> searchPosts(String searchType, String bbstitle, String bbscontent, BoardType type, Pageable pageable) {
        Page<BbsEntity> result;

        if (type != null) {
            result = switch (searchType.toLowerCase()) {
                case "title" -> bbsRepository.findByBulletinTypeAndBbstitleContaining(type, bbstitle, pageable);
                case "content" -> bbsRepository.findByBulletinTypeAndBbscontentContaining(type, bbscontent, pageable);
                case "title+content" -> {
                    if (bbstitle == null || bbscontent == null) {
                        throw new IllegalArgumentException("제목과 내용 검색어를 모두 입력하세요.");
                    }
                    yield bbsRepository.findByBulletinTypeAndTitleAndContent(type, bbstitle, pageable);
                }
                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        } else {
            result = switch (searchType.toLowerCase()) {
                case "title" -> bbsRepository.findByBbstitleContaining(bbstitle, pageable);
                case "content" -> bbsRepository.findByBbscontentContaining(bbscontent, pageable);
                case "title+content" -> {
                    if (bbstitle == null || bbscontent == null) {
                        throw new IllegalArgumentException("제목과 내용 검색어를 모두 입력하세요.");
                    }
                    yield bbsRepository.findByBbstitleContainingAndBbscontentContaining(bbstitle, bbscontent, pageable);
                }
                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        }
        return result.map(this::convertToDto);
    }
    private BbsDto convertToDto(BbsEntity e) {
        String originalName = null;
        String filteredName = null;
        if (e.getMemberNum() != null) {
            originalName = e.getMemberNum().getMemberName();
            filteredName = filterName(originalName);
        } else if (e.getAdminId() != null) {
            originalName = e.getAdminId().getAdminName();
            filteredName = originalName; // 관리자 이름은 마스킹 안 해도 되면 그대로 사용
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

        QandAEntity entity = QandAEntity.builder()
            .bbs(bbs)
            .question(dto.getQuestion())
            .answer(dto.getAnswer())
            .build();

        QandAEntity saved = qandARepository.save(entity);

        return QandADto.builder()
            .bulletinNum(saved.getBbs().getBulletinNum())
            .question(saved.getQuestion())
            .answer(saved.getAnswer())
            .build();
    }

    @Override
    public QandADto getQna(Long bbsId) {
        QandAEntity qna = qandARepository.findByBbsBulletinNum(bbsId)
            .orElseThrow(() -> new BbsException("QnA 없음"));

        return QandADto.builder()
            .bulletinNum(qna.getBbs().getBulletinNum())
            .question(qna.getQuestion())
            .answer(qna.getAnswer())
            .build();
    }
    
    @Override
    public void deleteQna(Long qnaId) {
        if (!qandARepository.existsById(qnaId)) {
            throw new BbsException("QnA 없음");
        }
        qandARepository.deleteById(qnaId);
    }

    @Override
    public QandADto updateQna(Long qnaId, QandADto dto) {
        QandAEntity qna = qandARepository.findById(qnaId)
            .orElseThrow(() -> new BbsException("QnA 없음"));

        qna.setQuestion(dto.getQuestion());
        qna.setAnswer(dto.getAnswer());

        return QandADto.builder()
            .bulletinNum(qna.getBbs().getBulletinNum())
            .question(qna.getQuestion())
            .answer(qna.getAnswer())
            .build();
    }
    
  /*  @Override
    public List<ImageBbsDto> saveImageFileList(Long bbsId, List<MultipartFile> files) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글 없음"));

        List<String> allowedExtensions = List.of("jpg", "jpeg");
        List<String> allowedMimeTypes = List.of("image/jpeg");
        long maxSize = 5 * 1024 * 1024;
        String uploadDir = "C:/Image"; // 실제 업로드 경로

        List<ImageBbsEntity> imageEntities = files.stream()
            .filter(file -> {
                String ext = getExtension(file.getOriginalFilename());
                String contentType = file.getContentType();
                return file != null && !file.isEmpty()
                        && ext != null && allowedExtensions.contains(ext.toLowerCase())
                        && contentType != null && allowedMimeTypes.contains(contentType.toLowerCase())
                        && file.getSize() <= maxSize;
            })
            .map(file -> {
                String ext = getExtension(file.getOriginalFilename());
                String savedName = UUID.randomUUID().toString() + "." + ext;
                Path target = Paths.get(uploadDir, savedName);

                try {
                    file.transferTo(target);
                } catch (IOException e) {
                    throw new BbsException("이미지 저장 실패: " + file.getOriginalFilename(), e);
                }

                return ImageBbsEntity.builder()
                    .bbs(bbs)
                    .thumbnailPath("/uploads/thumb/" + savedName) // 썸네일 경로 구성
                    .imagePath("/uploads/" + savedName)           // 원본 이미지 경로 구성
                    .build();
            })
            .collect(Collectors.toList());

        if (imageEntities.isEmpty()) {
            throw new BbsException("유효한 이미지 파일(jpg, jpeg)만 첨부할 수 있습니다.");
        }

        return imageBbsRepository.saveAll(imageEntities).stream()
            .map(entity -> ImageBbsDto.builder()
                .bulletinNum(entity.getBbs().getBulletinNum())
                .thumbnailPath(entity.getThumbnailPath())
                .imagePath(entity.getImagePath())
                .build())
            .collect(Collectors.toList());
    } */

    @Override
    public List<ImageBbsDto> getImageBbsList(Long bbsId) {
        return imageBbsRepository.findByBbsBulletinNum(bbsId).stream()
            .map(entity -> ImageBbsDto.builder()
                .bulletinNum(entity.getBbs().getBulletinNum())
                .thumbnailPath(entity.getThumbnailPath())
                .imagePath(entity.getImagePath())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(Long imageId) {
        // 1. DB에서 이미지 엔티티 조회
        ImageBbsEntity image = imageBbsRepository.findById(imageId)
            .orElseThrow(() -> new BbsException("이미지 없음"));

        // 2. 실제 파일 삭제
        try {
            String uploadDir = "C:/photo"; // 실제 파일 저장 경로

            // imagePath 삭제
            if (image.getImagePath() != null && !image.getImagePath().isBlank()) {
                String imageFileName = Paths.get(image.getImagePath()).getFileName().toString();
                Path imageFilePath = Paths.get(uploadDir, imageFileName);
                Files.deleteIfExists(imageFilePath);
            }

            // thumbnailPath 삭제
            if (image.getThumbnailPath() != null && !image.getThumbnailPath().isBlank()) {
                String thumbFileName = Paths.get(image.getThumbnailPath()).getFileName().toString();
                Path thumbFilePath = Paths.get(uploadDir, thumbFileName);
                Files.deleteIfExists(thumbFilePath);
            }

        } catch (IOException e) {
            throw new BbsException("이미지 파일 삭제 실패", e);
        }

        // 3. DB에서 삭제
        imageBbsRepository.deleteById(imageId);
    }


    @Transactional
    @Override
    public void deleteImages(List<Long> imageIds) {
        List<ImageBbsEntity> imagesToDelete = imageBbsRepository.findAllById(imageIds);

        if (imagesToDelete.isEmpty()) {
            throw new BbsException("삭제할 이미지가 존재하지 않습니다.");
        }

        Long bbsId = imagesToDelete.get(0).getBbs().getBulletinNum();
        boolean allSameBbs = imagesToDelete.stream()
            .allMatch(img -> img.getBbs().getBulletinNum().equals(bbsId));

        if (!allSameBbs) {
            throw new BbsException("서로 다른 게시글의 이미지를 동시에 삭제할 수 없습니다.");
        }

        long currentImageCount = imageBbsRepository.countByBbsBulletinNum(bbsId);

        if (currentImageCount - imagesToDelete.size() < 1) {
            throw new BbsException("게시글에는 최소 1장의 이미지가 있어야 합니다.");
        }

        // 실제 이미지 파일 삭제
        for (ImageBbsEntity image : imagesToDelete) {
            try {
                // 실제 파일 경로: 예를 들어 경로가 "/uploads/파일명"이라면 물리 경로로 변환
                String uploadDir = "C:/photo"; // 실제 파일 저장 폴더
                String filename = Paths.get(image.getImagePath()).getFileName().toString();
                Path filePath = Paths.get(uploadDir, filename);

                Files.deleteIfExists(filePath);

                // 썸네일 파일도 있다면 같이 삭제
                String thumbnailFilename = Paths.get(image.getThumbnailPath()).getFileName().toString();
                Path thumbnailPath = Paths.get(uploadDir, thumbnailFilename);
                Files.deleteIfExists(thumbnailPath);

            } catch (IOException e) {
                // 삭제 실패 로그 남기거나 예외처리
                System.err.println("파일 삭제 실패: " + e.getMessage());
            }
        }

        // DB 레코드 삭제
        imageBbsRepository.deleteAllById(imageIds);
    }


    @Override
    @Transactional
    public ImageBbsDto updateImage(Long imageId, ImageBbsDto dto, MultipartFile newFile) {
        ImageBbsEntity image = imageBbsRepository.findById(imageId)
            .orElseThrow(() -> new BbsException("이미지 없음"));

        if (newFile != null && !newFile.isEmpty()) {
            try {
                // 파일 확장자 추출
                String ext = getExtension(newFile.getOriginalFilename());
                // 저장할 파일명 (UUID + 확장자)
                String savedName = UUID.randomUUID().toString() + "." + ext;
                // 저장할 폴더 경로 (환경에 맞게 변경)
                String uploadDir = "C:/photo";

                // 실제 저장 경로
                Path savedPath = Paths.get(uploadDir, savedName);

                // 서버에 파일 저장
                newFile.transferTo(savedPath);

                // DB에 저장할 경로 (예: 웹 접근용 URL 경로)
                String dbImagePath = "/uploads/" + savedName;
                String dbThumbnailPath = "/uploads/thumb_" + savedName; // 썸네일 경로가 있다면 따로 처리

                // DB 엔티티 업데이트
                image.setImagePath(dbImagePath);
                image.setThumbnailPath(dbThumbnailPath); // 썸네일 생성 로직 있으면 호출 후 경로 세팅

            } catch (IOException e) {
                throw new BbsException("이미지 파일 저장 실패", e);
            }
        } else {
            // newFile이 없으면 DTO에서 넘어온 경로만 DB에 업데이트할 수도 있고, 필요에 따라 처리
            image.setImagePath(dto.getImagePath());
            image.setThumbnailPath(dto.getThumbnailPath());
        }

        // 변경된 엔티티는 트랜잭션 커밋 시 자동 저장됨

        return ImageBbsDto.builder()
            .bulletinNum(image.getBbs().getBulletinNum())
            .thumbnailPath(image.getThumbnailPath())
            .imagePath(image.getImagePath())
            .build();
    }


    @Override
    public List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("해당 게시글이 존재하지 않습니다"));

        // 게시판 유형별 허용 확장자 및 MIME 타입 정의
        List<String> allowedExtensions;
        List<String> allowedMimeTypes;

        switch (boardType) {
            case POTO:

                allowedExtensions = List.of("jpg", "jpeg");

                allowedMimeTypes = List.of("image/jpeg");

                break; 
            case FAQ:
            case NORMAL:
                allowedExtensions = List.of("jpg", "jpeg", "pdf", "ppt", "pptx", "doc", "docx");
                allowedMimeTypes = List.of(
                    "image/jpeg",
                    "application/pdf",
                    "application/vnd.ms-powerpoint", // ppt
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation", // pptx
                    "application/msword", // doc
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // docx
                );
                break;
            default:
                throw new BbsException("지원하지 않는 게시판 타입입니다.");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        String uploadDir = "C:/photo"; // 실제 경로로 변경 필요
        List<FileUpLoadEntity> entities = files.stream()
            .filter(file -> {
                if (file == null || file.isEmpty()) return false;
                String ext = getExtension(file.getOriginalFilename());
                String contentType = file.getContentType();

                return ext != null && allowedExtensions.contains(ext.toLowerCase())
                        && contentType != null && allowedMimeTypes.contains(contentType.toLowerCase())
                        && file.getSize() <= maxSize;
            })
            .map(file -> {
                String ext = getExtension(file.getOriginalFilename());
                String savedName = UUID.randomUUID().toString() + "." + ext;
                Path target = Paths.get(uploadDir, savedName);

                try {
                    file.transferTo(target);
                } catch (IOException e) {
                    throw new BbsException("파일 저장 실패: " + file.getOriginalFilename(), e);
                }

                return FileUpLoadEntity.builder()
                    .bbs(bbs)
                    .originalName(file.getOriginalFilename())
                    .savedName(savedName)
                    .path(uploadDir)
                    .size(file.getSize())
                    .extension(ext)
                    .build();
            })
            .collect(Collectors.toList());

        if (entities.isEmpty()) {
            throw new BbsException("허용된 파일 형식만 첨부할 수 있습니다.");
        }

        return fileUploadRepository.saveAll(entities).stream()
            .map(entity -> FileUpLoadDto.dtoBuilder()
                .fileNum(entity.getFilenum())
                .originalName(entity.getOriginalName())
                .savedName(entity.getSavedName())
                .path(entity.getPath())
                .size(entity.getSize())
                .extension(entity.getExtension())
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
                .path(entity.getPath())
                .size(entity.getSize())
                .extension(entity.getExtension())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(Long fileId) {
        if (!fileUploadRepository.existsById(fileId)) {
            throw new BbsException("파일 없음");
        }
        fileUploadRepository.deleteById(fileId);
    }
    
    @Override
    @Transactional
    public FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto, MultipartFile newFile) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new BbsException("파일 없음"));

        // 실제 파일 저장
        if (newFile != null && !newFile.isEmpty()) {
            try {
                String ext = getExtension(newFile.getOriginalFilename());
                String savedName = UUID.randomUUID() + "." + ext;
                String uploadDir = "C:/photo"; // 또는 네 환경에 맞는 경로

                Path path = Paths.get(uploadDir, savedName);
                newFile.transferTo(path);  // 실제 파일 저장

                // 파일 메타데이터 업데이트
                file.setOriginalName(newFile.getOriginalFilename());
                file.setSavedName(savedName);
                file.setPath("/uploads/" + savedName);
                file.setSize(newFile.getSize());
                file.setExtension(ext);

            } catch (IOException e) {
                throw new BbsException("파일 저장 실패", e);
            }
        }

        return FileUpLoadDto.dtoBuilder()
            .fileNum(file.getFilenum())
            .originalName(file.getOriginalName())
            .savedName(file.getSavedName())
            .path(file.getPath())
            .size(file.getSize())
            .extension(file.getExtension())
            .build();
    }
}