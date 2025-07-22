package com.project.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.admin.entity.AdminEntity;
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



    // ---------------- BBS ----------------
    public BbsDto createBbs(BbsDto dto) {
        BbsEntity entity = BbsEntity.builder()
            .bulletinnum(dto.getBulletinNum())
            .bbstitle(dto.getBbsTitle())
            .bbscontent(dto.getBbsContent())
            .registdate(dto.getRegistDate())
            .revisiondate(dto.getRevisionDate())
            .deldate(dto.getDelDate())
            .viewers(dto.getViewers())
            .bulletinType(dto.getBulletinType())
            .admin(AdminEntity.builder().adminId(dto.getAdminId()).build())
            .member(Member.builder().memberNum(dto.getMemberNum()).build())
            .build();

        BbsEntity saved = bbsRepository.save(entity);
        return convertToDto(saved);
    }
    
    // 게시글 수정
    @Transactional
    public BbsDto updateBbs(Long bbsId, BbsDto dto) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글을 찾을 수 없습니다: " + bbsId));

        bbs.setBbstitle(dto.getBbsTitle());
        bbs.setBbscontent(dto.getBbsContent());
        bbs.setRevisiondate(dto.getRevisionDate());

        BbsEntity updated = bbsRepository.save(bbs);
        return convertToDto(updated);
    }
    
    //게시글삭제기능
    public void deleteBbsByIds(List<Long> ids) {
        ids.forEach(id -> {
            fileUploadRepository.deleteByBbs_BulletinNum(id);
            imageBbsRepository.deleteByBbs_BulletinNum(id);
            qandARepository.deleteByBbs_BulletinNum(id);
            bbsRepository.deleteById(id);
        });
    }
    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public BbsDto getBbs(Long id) {
        BbsEntity bbs = bbsRepository.findById(id)
            .orElseThrow(() -> new BbsException("게시글을 찾을 수 없습니다: " + id));
        return convertToDto(bbs);
    }

    // 게시글 타입별 조회
    @Transactional(readOnly = true)
    public List<BbsDto> getAllByType(BoardType type) {
        return bbsRepository.findByBulletinType(type).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    
    @Transactional(readOnly = true)
    public Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable) {
        Pageable sortedPageable;
        
        if ("views".equalsIgnoreCase(sort)) {
            sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "viewers")
            );
        } else {
            sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "registDate")
            );
        }

        Page<BbsEntity> page;
        if (type != null) {
            page = bbsRepository.findByBulletinType(type, sortedPageable);
        } else {
            page = bbsRepository.findAll(sortedPageable);
        }
        return page.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<BbsDto> searchPosts(String searchType, String keyword, BoardType type, Pageable pageable) {
        Page<BbsEntity> result;

        if (type != null) {
            switch (searchType.toLowerCase()) {
                case "title":
                    result = bbsRepository.findByBulletinTypeAndBbstitleContaining(type, keyword, pageable);
                    break;
                case "author":
                    result = bbsRepository.findByBulletinTypeAndMember_MemberNameContaining(type, keyword, pageable);
                    break;
                case "content":
                    result = bbsRepository.findByBulletinTypeAndBbscontentContaining(type, keyword, pageable);
                    break;
                case "title+content":
                    // JPQL 커스텀 메서드 사용
                    result = bbsRepository.findByBulletinTypeAndTitleOrContent(type, keyword, pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid search type: " + searchType);
            }
        } else {
            // 타입 필터 없을 때
            switch (searchType.toLowerCase()) {
                case "title":
                    result = bbsRepository.findByBbstitleContaining(keyword, pageable);
                    break;
                case "author":
                    result = bbsRepository.findByMember_MemberNameContaining(keyword, pageable);
                    break;
                case "content":
                    result = bbsRepository.findByBbscontentContaining(keyword, pageable);
                    break;
                case "title+content":
                    result = bbsRepository.findByBbstitleContainingOrBbscontentContaining(keyword, keyword, pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid search type: " + searchType);
            }
        }

        return result.map(this::convertToDto);
    }
    
//    public Page<BbsDto> getAllWithPaging(Pageable pageable) {
//        return bbsRepository.findAll(pageable)
//                .map(this::convertToDto);
//    }
    
    @Transactional(readOnly = true)
    public Page<BbsDto> searchPosts(String type, String keyword, Pageable pageable) {
        Page<BbsEntity> result;
        switch (type.toLowerCase()) {
            case "title":
                result = bbsRepository.findByBbstitleContaining(keyword, pageable);
                break;
            case "author":
                result = bbsRepository.findByMember_MemberNameContaining(keyword, pageable);
                break;
            case "content":
                result = bbsRepository.findByBbscontentContaining(keyword, pageable);
                break;
            case "title+content":
                result = bbsRepository.findByBbstitleContainingOrBbscontentContaining(keyword, keyword, pageable);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + type);
        }
        return result.map(this::convertToDto);
    }
    
    
    // Entity → DTO 변환 메서드
    private BbsDto convertToDto(BbsEntity entity) {
        return BbsDto.builder()
            .bulletinNum(entity.getBulletinnum())
            .bbsTitle(entity.getBbstitle())
            .bbsContent(entity.getBbscontent())
            .registDate(entity.getRegistdate())
            .revisionDate(entity.getRevisiondate())
            .delDate(entity.getDeldate())
            .viewers(entity.getViewers())
            .bulletinType(entity.getBulletinType())
            .adminId(entity.getAdminId())
            .memberNum(entity.getMember().getMemberNum())
            .build();
    }

    // ---------------- QnA ----------------
    public QandADto saveQna(Long bbsId, QandADto dto) {
        BbsEntity bbs = bbsRepository.findById(bbsId)
            .orElseThrow(() -> new BbsException("게시글 없음"));

        QandAEntity entity = QandAEntity.builder()
            .bbs(bbs)
            .question(dto.getQuestion())
            .answer(dto.getAnswer())
            .build();

        QandAEntity saved = qandARepository.save(entity);
        
        return QandADto.builder()
            .bulletinNum(entity.getBbs().getBulletinnum())
            .question(entity.getQuestion())
            .answer(entity.getAnswer())
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

    // ---------------- Image ----------------
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

    // ---------------- File ----------------
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
}
