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
    public BbsDto updateBbs(Long id, BbsDto dto, Long userId, List<MultipartFile> newFiles, List<Long> deleteFileIds, boolean isAdmin) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));
        
        if (isAdmin) {
            // 관리자라면, 관리자 아이디가 맞는지 확인 (필요하다면)
            if (bbs.getAdminId() == null || !bbs.getAdminId().getAdminId().equals(userId)) {
                throw new BbsException("관리자 권한이 없습니다.");
            }
        } else {
            // 회원이라면 본인 확인
            if (bbs.getMemberNum() == null || !bbs.getMemberNum().getMemberNum().equals(userId)) {
                throw new BbsException("본인이 작성한 글만 수정할 수 있습니다.");
            }
        }

        // 게시글 업데이트
        bbs.setBbstitle(dto.getBbsTitle());
        bbs.setBbscontent(dto.getBbsContent());
        bbs.setRevisiondate(dto.getRevisionDate());

        // 삭제할 파일 처리
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (Long fileId : deleteFileIds) {
                this.deleteFileById(fileId);
            }
        }

        // 새 파일 업로드 처리
        if (newFiles != null && !newFiles.isEmpty()) {
            this.saveFileList(bbs.getBulletinNum(), newFiles, bbs.getBulletinType());
        }

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
    @Transactional
    public void deleteBbsMultiple(List<Long> ids, Long requesterMemberNum, Long requesterAdminId) {
        if (requesterAdminId == null) {
            throw new BbsException("관리자 권한이 필요합니다.");
        }

        String uploadDir = "C:/photo"; // 실제 파일 저장 경로

        for (Long id : ids) {
            BbsEntity bbs = bbsRepository.findById(id)
                .orElseThrow(() -> new BbsException("게시글 없음: " + id));

            // 작성자 회원 존재 여부 확인 (필요시)
            if (bbs.getMemberNum() != null) {
                memberRepository.findById(bbs.getMemberNum().getMemberNum())
                    .orElseThrow(() -> new BbsException("작성자 회원 정보가 존재하지 않습니다."));
            }

            // 관리자만 삭제 가능하므로 작성자 권한 체크는 생략 가능
            boolean isAdmin = requesterAdminId != null;
            if (!isAdmin) {
                throw new BbsException("삭제 권한이 없습니다.");
            }

            // 1. QANDA 게시판 관련 데이터 삭제
            if (bbs.getBulletinType() == BoardType.FAQ) {
                qandARepository.deleteByBbsBulletinNum(bbs.getBulletinNum());
            }

            // 2. 첨부파일 삭제
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
                default:
                    // 기타 게시판 타입이 있으면 여기에 처리
                    break;
            }

            // 3. POTO 게시판 대표 이미지 + 썸네일 삭제
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

            // 4. 게시글 삭제
            bbsRepository.deleteById(id);
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

        String typeLower = searchType == null ? "all" : searchType.toLowerCase();

        if (type != null) {
            // 게시판 타입이 지정된 경우
            result = switch (typeLower) {
                case "title" ->
                    bbsRepository.findByBulletinTypeAndBbstitleContaining(type, bbstitle, pageable);
                case "content" ->
                    bbsRepository.findByBulletinTypeAndBbscontentContaining(type, bbscontent, pageable);
                case "title+content" -> {
                    if (bbstitle == null || bbscontent == null) {
                        throw new IllegalArgumentException("제목과 내용 검색어를 모두 입력하세요.");
                    }
                    yield bbsRepository.findByBulletinTypeAndBbstitleContainingAndBbscontentContaining(type, bbstitle, bbscontent, pageable);
                }
                case "all" ->
                    bbsRepository.findByBulletinType(type, pageable);
                default ->
                    throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        } else {
            // 게시판 타입이 없는 경우
            result = switch (typeLower) {
                case "title" ->
                    bbsRepository.findByBbstitleContaining(bbstitle, pageable);
                case "content" ->
                    bbsRepository.findByBbscontentContaining(bbscontent, pageable);
                case "title+content" -> {
                    if (bbstitle == null || bbscontent == null) {
                        throw new IllegalArgumentException("제목과 내용 검색어를 모두 입력하세요.");
                    }
                    yield bbsRepository.findByBbstitleContainingAndBbscontentContaining(bbstitle, bbscontent, pageable);
                }
                case "all" ->
                    bbsRepository.findAll(pageable);
                default ->
                    throw new IllegalArgumentException("Invalid search type: " + searchType);
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
    public void deleteQna(Long qnaId, Long adminId) {
        QandAEntity qna = qandARepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA 답변이 존재하지 않습니다."));

        // 관리자 권한 체크 (필요하면 로직 추가)
        // 예: adminId가 null이거나, 권한 없는 경우 예외 처리
        if (adminId == null) {
            throw new IllegalArgumentException("관리자 ID가 필요합니다.");
        }

        qandARepository.delete(qna);
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
    @Transactional
    public ImageBbsDto updateImage(Long bulletinNum, ImageBbsDto dto, MultipartFile newFile) {
        // bulletinNum 으로 이미지 리스트 조회
        List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(bulletinNum);

        if (images.isEmpty()) {
            throw new BbsException("해당 게시글에 이미지가 없습니다.");
        }

        // 우선 첫 번째 이미지 선택 (특정 이미지 지정 로직이 없으므로)
        ImageBbsEntity image = images.get(0);

        if (newFile != null && !newFile.isEmpty()) {
            try {
                String ext = getExtension(newFile.getOriginalFilename());
                String savedName = UUID.randomUUID().toString() + "." + ext;
                String uploadDir = "C:/photo";
                Path savedPath = Paths.get(uploadDir, savedName);

                newFile.transferTo(savedPath);

                String dbImagePath = "/uploads/" + savedName;
                String dbThumbnailPath = "/uploads/thumb_" + savedName;

                image.setImagePath(dbImagePath);
                image.setThumbnailPath(dbThumbnailPath);
            } catch (IOException e) {
                throw new BbsException("이미지 파일 저장 실패", e);
            }
        } else {
            image.setImagePath(dto.getImagePath());
            image.setThumbnailPath(dto.getThumbnailPath());
        }

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
    @Transactional
    public void deleteFileById(Long fileId) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new BbsException("삭제할 파일이 존재하지 않습니다: " + fileId));

        // 실제 저장소 파일 삭제 로직이 있다면 추가 (예: 파일 시스템에서 삭제)
        // 예) Files.deleteIfExists(Paths.get(file.getPath(), file.getSavedName()));

        fileUploadRepository.delete(file);
    }

}