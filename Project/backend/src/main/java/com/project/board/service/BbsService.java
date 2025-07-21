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
import com.project.admin.AdminEntity; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BbsService {
	 private final BbsRepository bbsRepository;
	    private final QandARepository qandARepository;
	    private final ImageBbsRepository imageBbsRepository;
	    private final FileUpLoadRepository fileUploadRepository;

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
	            .adminId(AdminEntity.builder().adminId(dto.getAdminId()).build())
	            .memberNum(MemberEntity.builder().memberNum(dto.getMemberNum()).build())
	            .build();

	        return convertToDto(bbsRepository.save(entity));
	    }

	    @Transactional
	    public BbsDto updateBbs(Long id, BbsDto dto) {
	        BbsEntity bbs = bbsRepository.findById(id).orElseThrow(() -> new BbsException("게시글 없음: " + id));
	        bbs.setBbstitle(dto.getBbsTitle());
	        bbs.setBbscontent(dto.getBbsContent());
	        bbs.setRevisiondate(dto.getRevisionDate());
	        return convertToDto(bbsRepository.save(bbs));
	    }

	    public void deleteBbsByIds(List<Long> ids) {
	        ids.forEach(id -> {
	            fileUploadRepository.deleteByBbs_BulletinNum(id);
	            imageBbsRepository.deleteByBbs_BulletinNum(id);
	            qandARepository.deleteByBbs_BulletinNum(id);
	            bbsRepository.deleteById(id);
	        });
	    }

	    public BbsDto getBbs(Long id) {
	        return bbsRepository.findById(id).map(this::convertToDto)
	                .orElseThrow(() -> new BbsException("게시글 없음: " + id));
	    }

	    public List<BbsDto> getAllByType(BoardType type) {
	        return bbsRepository.findByBulletinType(type).stream().map(this::convertToDto).collect(Collectors.toList());
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
	        return BbsDto.builder()
	            .bulletinNum(e.getBulletinnum())
	            .bbsTitle(e.getBbstitle())
	            .bbsContent(e.getBbscontent())
	            .registDate(e.getRegistdate())
	            .revisionDate(e.getRevisiondate())
	            .delDate(e.getDeldate())
	            .viewers(e.getViewers())
	            .bulletinType(e.getBulletinType())
	            .adminId(e.getAdminId().getAdminId())
	            .memberNum(e.getMemberNum().getMemberNum())
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