package com.project.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BbsService {
    private final BbsRepository bbsRepository;
    private final QandARepository qandARepository;
    private final ImageBbsRepository imageBbsRepository;
    private final FileUpLoadRepository fileUploadRepository;

    public BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId) {
        BoardType type = dto.getBulletinType();

        if (type == BoardType.NORMAL && requesterAdminId == null) {
            throw new BbsException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        if ((type == BoardType.FAQ || type == BoardType.POTO) && requesterMemberNum == null) {
            throw new BbsException("해당 게시판은 회원만 작성할 수 있습니다.");
        }

        if (type == BoardType.POTO) {
            List<ImageBbsEntity> images = imageBbsRepository.findByBbs_BulletinNum(dto.getBulletinNum());
            if (images == null || images.isEmpty()) {
                throw new BbsException("이미지 게시판은 최소 1장 이상의 사진을 등록해야 합니다.");
            }
        }

        BbsEntity entity = BbsEntity.builder()
            .bulletinnum(dto.getBulletinNum())
            .bbstitle(dto.getBbsTitle())
            .bbscontent(dto.getBbsContent())
            .registdate(dto.getRegistDate())
            .revisiondate(dto.getRevisionDate())
            .deldate(dto.getDelDate())
            .viewers(dto.getViewers())
            .bulletinType(dto.getBulletinType())
            .memberNum(requesterMemberNum != null ? MemberEntity.builder().memberNum(requesterMemberNum).build() : null)
            .build();

        return convertToDto(bbsRepository.save(entity));
    }

    @Transactional
    public BbsDto updateBbs(Long id, BbsDto dto, Long memberNum, String password) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));

        if (!bbs.getMemberNum().getMemberNum().equals(memberNum)) {
            throw new BbsException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        // 비밀번호 검증 제거

        bbs.setBbstitle(dto.getBbsTitle());
        bbs.setBbscontent(dto.getBbsContent());
        bbs.setRevisiondate(dto.getRevisionDate());

        return convertToDto(bbsRepository.save(bbs));
    }

    public void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));

        boolean isAdmin = requesterAdminId != null;
        boolean isAuthor = requesterMemberNum != null && bbs.getMemberNum().getMemberNum().equals(requesterMemberNum);

        if (isAdmin || isAuthor) {
            fileUploadRepository.deleteByBbs_BulletinNum(id);
            imageBbsRepository.deleteByBbs_BulletinNum(id);
            qandARepository.deleteByBbs_BulletinNum(id);
            bbsRepository.deleteById(id);
        } else {
            throw new BbsException("삭제 권한이 없습니다.");
        }
    }

    public void deleteBbs(List<Long> ids, Long requesterAdminId, Long requesterMemberNum) {
        for (Long id : ids) {
            deleteBbs(id, requesterMemberNum, requesterAdminId);
        }
    }

    public BbsDto getBbs(Long id) {
        return bbsRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new BbsException("게시글 없음: " + id));
    }

    public List<BbsDto> getAllByType(BoardType type) {
        return bbsRepository.findByBulletinType(type).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable) {
        Sort sorted = "views".equals(sort) ? Sort.by("viewers").descending() : Sort.by("registdate").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorted);

        Page<BbsEntity> page = (type != null)
            ? bbsRepository.findByBulletinType(type, sortedPageable)
            : bbsRepository.findAll(sortedPageable);

        return page.map(this::convertToDto);
    }

    public Page<BbsDto> searchPosts(String searchType, String keyword, BoardType type, Pageable pageable) {
        Page<BbsEntity> result;
        if (type != null) {
            result = switch (searchType.toLowerCase()) {
                case "title" -> bbsRepository.findByBulletinTypeAndBbstitleContaining(type, keyword, pageable);
                case "author" -> bbsRepository.findByBulletinTypeAndMember_MemberNameContaining(type, keyword, pageable);
                case "content" -> bbsRepository.findByBulletinTypeAndBbscontentContaining(type, keyword, pageable);
                case "title+content" -> bbsRepository.findByBulletinTypeAndTitleOrContent(type, keyword, pageable);
                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        } else {
            result = switch (searchType.toLowerCase()) {
                case "title" -> bbsRepository.findByBbstitleContaining(keyword, pageable);
                case "author" -> bbsRepository.findByMember_MemberNameContaining(keyword, pageable);
                case "content" -> bbsRepository.findByBbscontentContaining(keyword, pageable);
                case "title+content" -> bbsRepository.findByBbstitleContainingOrBbscontentContaining(keyword, keyword, pageable);
                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        }
        return result.map(this::convertToDto);
    }

    private BbsDto convertToDto(BbsEntity e) {
        String originalName = e.getMemberNum().getMemberName();
        String filteredName = filterName(originalName);
        return BbsDto.builder()
                .bulletinNum(e.getBulletinnum())
                .bbsTitle(e.getBbstitle())
                .bbsContent(e.getBbscontent())
                .registDate(e.getRegistdate())
                .revisionDate(e.getRevisiondate())
                .delDate(e.getDeldate())
                .viewers(e.getViewers())
                .bulletinType(e.getBulletinType())
                .adminId(e.getAdminId() != null ? e.getAdminId().getAdminId() : null)
                .memberNum(e.getMemberNum().getMemberNum())
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

    public QandADto saveQna(Long bbsId, QandADto dto, Long requesterAdminId) {
        if (requesterAdminId == null) {
            throw new BbsException("QnA 답변은 관리자만 작성할 수 있습니다.");
        }

        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글 없음"));

        QandAEntity entity = QandAEntity.builder()
            .bbs(bbs)
            .question(dto.getQuestion())
            .answer(dto.getAnswer())
            .build();

        QandAEntity saved = qandARepository.save(entity);

        return QandADto.builder()
            .bulletinNum(saved.getBbs().getBulletinnum())
            .question(saved.getQuestion())
            .answer(saved.getAnswer())
            .build();
    }

    public QandADto getQna(Long bbsId) {
        QandAEntity qna = qandARepository.findByBbs_BulletinNum(bbsId)
            .orElseThrow(() -> new BbsException("QnA 없음"));

        return QandADto.builder()
            .bulletinNum(qna.getBbs().getBulletinnum())
            .question(qna.getQuestion())
            .answer(qna.getAnswer())
            .build();
    }

    public void deleteQna(Long qnaId) {
        if (!qandARepository.existsById(qnaId)) {
            throw new BbsException("QnA 없음");
        }
        qandARepository.deleteById(qnaId);
    }

    public QandADto updateQna(Long qnaId, QandADto dto) {
        QandAEntity qna = qandARepository.findById(qnaId)
            .orElseThrow(() -> new BbsException("QnA 없음"));

        qna.setQuestion(dto.getQuestion());
        qna.setAnswer(dto.getAnswer());

        return QandADto.builder()
            .bulletinNum(qna.getBbs().getBulletinnum())
            .question(qna.getQuestion())
            .answer(qna.getAnswer())
            .build();
    }

    public List<ImageBbsDto> saveImageBbsList(Long bbsId, List<ImageBbsDto> dtos) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글 없음"));

        List<ImageBbsEntity> entities = dtos.stream()
            .map(dto -> ImageBbsEntity.builder()
                .bbs(bbs)
                .thumbnailPath(dto.getThumbnailPath())
                .imagePath(dto.getImagePath())
                .build())
            .collect(Collectors.toList());

        return imageBbsRepository.saveAll(entities).stream()
            .map(entity -> ImageBbsDto.builder()
                .bulletinNum(entity.getBbs().getBulletinnum())
                .thumbnailPath(entity.getThumbnailPath())
                .imagePath(entity.getImagePath())
                .build())
            .collect(Collectors.toList());
    }

    public List<ImageBbsDto> getImageBbsList(Long bbsId) {
        return imageBbsRepository.findByBbs_BulletinNum(bbsId).stream()
            .map(entity -> ImageBbsDto.builder()
                .bulletinNum(entity.getBbs().getBulletinnum())
                .thumbnailPath(entity.getThumbnailPath())
                .imagePath(entity.getImagePath())
                .build())
            .collect(Collectors.toList());
    }

    public void deleteImage(Long imageId) {
        if (!imageBbsRepository.existsById(imageId)) {
            throw new BbsException("이미지 없음");
        }
        imageBbsRepository.deleteById(imageId);
    }

    public ImageBbsDto updateImage(Long imageId, ImageBbsDto dto) {
        ImageBbsEntity image = imageBbsRepository.findById(imageId)
            .orElseThrow(() -> new BbsException("이미지 없음"));

        image.setThumbnailPath(dto.getThumbnailPath());
        image.setImagePath(dto.getImagePath());

        return ImageBbsDto.builder()
            .bulletinNum(image.getBbs().getBulletinnum())
            .thumbnailPath(image.getThumbnailPath())
            .imagePath(image.getImagePath())
            .build();
    }

    public List<FileUpLoadDto> saveFileList(Long bbsId, List<FileUpLoadDto> dtos) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글 없음"));

        List<FileUpLoadEntity> entities = dtos.stream()
            .map(dto -> FileUpLoadEntity.builder()
                .bbs(bbs)
                .originalName(dto.getOriginalName())
                .savedName(dto.getSavedName())
                .path(dto.getPath())
                .size(dto.getSize())
                .extension(dto.getExtension())
                .build())
            .collect(Collectors.toList());

        return fileUploadRepository.saveAll(entities).stream()
            .map(entity -> FileUpLoadDto.builder()
                .fileNum(entity.getFilenum())
                .originalName(entity.getOriginalName())
                .savedName(entity.getSavedName())
                .path(entity.getPath())
                .size(entity.getSize())
                .extension(entity.getExtension())
                .build())
            .collect(Collectors.toList());
    }

    public List<FileUpLoadDto> getFilesByBbs(Long bbsId) {
        return fileUploadRepository.findByBbs_BulletinNum(bbsId).stream()
            .map(entity -> FileUpLoadDto.builder()
                .fileNum(entity.getFilenum())
                .originalName(entity.getOriginalName())
                .savedName(entity.getSavedName())
                .path(entity.getPath())
                .size(entity.getSize())
                .extension(entity.getExtension())
                .build())
            .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId) {
        if (!fileUploadRepository.existsById(fileId)) {
            throw new BbsException("파일 없음");
        }
        fileUploadRepository.deleteById(fileId);
    }

    public FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto) {
        FileUpLoadEntity file = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new BbsException("파일 없음"));

        file.setOriginalName(dto.getOriginalName());
        file.setSavedName(dto.getSavedName());
        file.setPath(dto.getPath());
        file.setSize(dto.getSize());
        file.setExtension(dto.getExtension());

        return FileUpLoadDto.builder()
            .fileNum(file.getFilenum())
            .originalName(file.getOriginalName())
            .savedName(file.getSavedName())
            .path(file.getPath())
            .size(file.getSize())
            .extension(file.getExtension())
            .build();
    }
}
