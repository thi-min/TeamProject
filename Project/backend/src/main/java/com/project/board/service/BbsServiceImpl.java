package com.project.board.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
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
    

    @Override
    public BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId) {
        BoardType type = dto.getBulletinType();

        if (type == BoardType.NORMAL && requesterAdminId == null) {
            throw new BbsException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        if ((type == BoardType.FAQ || type == BoardType.POTO) && requesterMemberNum == null) {
            throw new BbsException("해당 게시판은 회원만 작성할 수 있습니다.");
        }

        if (type == BoardType.POTO) {
            List<ImageBbsEntity> images = imageBbsRepository.findByBbsBulletinNum(dto.getBulletinNum());
            if (images == null || images.isEmpty()) {
                throw new BbsException("이미지 게시판은 최소 1장 이상의 사진을 등록해야 합니다.");
            }
        }
        
     // DTO에서 받은 memberNum(Long) -> MemberEntity 조회 후 엔티티에 세팅
        MemberEntity member = null;
        if(dto.getMemberNum() != null){
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

        return convertToDto(bbsRepository.save(entity));
    }
    @Transactional
    public BbsDto createBbsWithFiles(BbsDto dto, Long requesterMemberNum, Long requesterAdminId, List<MultipartFile> files) {
        // 1. 게시글 저장 (기존 createBbs 호출)
        BbsDto savedBbs = createBbs(dto, requesterMemberNum, requesterAdminId);

        if (files != null && !files.isEmpty()) {
            BoardType type = dto.getBulletinType();

            switch (type) {
                case POTO:
                    // 이미지 게시판: 이미지 테이블에만 저장, 본문 삽입은 하지 않음
                    saveImageFileList(savedBbs.getBulletinNum(), files);
                    break;

                case FAQ:
                case NORMAL:
                    // 파일 업로드 처리
                    List<FileUpLoadDto> uploadedFiles = saveFileList(savedBbs.getBulletinNum(), files, type);

                    // 본문에 파일 링크 삽입
                    String updatedContent = insertFilesToContent(savedBbs.getBbsContent(), uploadedFiles);

                    // DB 업데이트
                    BbsEntity bbsEntity = bbsRepository.findById(savedBbs.getBulletinNum())
                        .orElseThrow(() -> new BbsException("게시글이 존재하지 않습니다."));
                    bbsEntity.setBbscontent(updatedContent);
                    bbsRepository.save(bbsEntity);

                    // DTO 업데이트
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
    public void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));

        // 작성자 회원이 실제 존재하는지 확인
        if (bbs.getMemberNum() != null) {
            memberRepository.findById(bbs.getMemberNum().getMemberNum())
                .orElseThrow(() -> new BbsException("작성자 회원 정보가 존재하지 않습니다."));
        }

        boolean isAdmin = requesterAdminId != null;
        boolean isAuthor = false;
        if (requesterMemberNum != null && bbs.getMemberNum() != null) {
            isAuthor = requesterMemberNum.equals(bbs.getMemberNum().getMemberNum());
        }

        if (isAdmin || isAuthor) {
        	qandARepository.deleteByBbsBulletinNum(id);
            fileUploadRepository.deleteByBbsBulletinNum(id);
            imageBbsRepository.deleteByBbsBulletinNum(id);
            bbsRepository.deleteById(id);
        } else {
            throw new BbsException("삭제 권한이 없습니다.");
        }
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

    @Override
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
    }

   
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
        if (!imageBbsRepository.existsById(imageId)) {
            throw new BbsException("이미지 없음");
        }
        imageBbsRepository.deleteById(imageId);
    }
    
    @Transactional
    @Override
    public void deleteImages(List<Long> imageIds) {
        List<ImageBbsEntity> imagesToDelete = imageBbsRepository.findAllById(imageIds);

        if (imagesToDelete.isEmpty()) {
            throw new BbsException("삭제할 이미지가 존재하지 않습니다.");
        }

        // 게시글 아이디 가져오기 (삭제할 이미지들이 모두 같은 게시글에 속하는지 확인)
        Long bbsId = imagesToDelete.get(0).getBbs().getBulletinNum();

        boolean allSameBbs = imagesToDelete.stream()
            .allMatch(img -> img.getBbs().getBulletinNum().equals(bbsId));

        if (!allSameBbs) {
            throw new BbsException("서로 다른 게시글의 이미지를 동시에 삭제할 수 없습니다.");
        }

        // 게시글에 현재 이미지 개수
        long currentImageCount = imageBbsRepository.countByBbsBulletinNum(bbsId);

        // 삭제 후 이미지가 최소 1장 남아야 한다는 조건 검사
        if (currentImageCount - imagesToDelete.size() < 1) {
            throw new BbsException("게시글에는 최소 1장의 이미지가 있어야 합니다.");
        }

        // 실제 삭제 수행
        imageBbsRepository.deleteAllById(imageIds);
    }

    @Override
    public ImageBbsDto updateImage(Long imageId, ImageBbsDto dto) {
        ImageBbsEntity image = imageBbsRepository.findById(imageId)
            .orElseThrow(() -> new BbsException("이미지 없음"));

        image.setThumbnailPath(dto.getThumbnailPath());
        image.setImagePath(dto.getImagePath());

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
          /*  case POTO:
                allowedExtensions = List.of("jpg", "jpeg");
                allowedMimeTypes = List.of("image/jpeg");
                break; */
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
    public FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new BbsException("파일 없음"));

        file.setOriginalName(dto.getOriginalName());
        file.setSavedName(dto.getSavedName());
        file.setPath(dto.getPath());
        file.setSize(dto.getSize());
        file.setExtension(dto.getExtension());

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