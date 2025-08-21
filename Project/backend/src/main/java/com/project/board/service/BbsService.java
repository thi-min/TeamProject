package com.project.board.service;

import com.project.board.BoardType;
import com.project.board.dto.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BbsService {
	
    BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId, List<MultipartFile> files, List<String> insertOptions); // 게시글 생성 (회원 또는 관리자)
    
    BbsDto createPotoBbs(BbsDto dto, Long requesterMemberNum, List<MultipartFile> files);
    
    BbsDto createBbsWithFiles(BbsDto dto, Long requesterMemberNum, Long requesterAdminId, List<MultipartFile> files,  List<String> insertOptions); // 생성된 게시글 + 파일첨부 (회원 또는 관리자)
    
    BbsDto updateBbs(Long id, BbsDto dto, Long userId, List<MultipartFile> newFiles, List<Long> deleteFileIds, boolean isAdmin, List<String> insertOptions); // 게시글 수정 (작성자 본인만 가능)
    
    void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId);  // 게시글 단건 삭제 (작성자 본인 또는 관리자)
    
    void deleteBbsMultiple(List<Long> ids, Long requesterMemberNum, Long requesterAdminId); //게시글 복수건 삭제(관리자)
    
    BbsDto getBbs(Long id); // 게시글 단건 조회
    
    List<BbsDto> getAllByType(BoardType type); // 특정 게시판 타입으로 모든 게시글 리스트 조회
    
    Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable); // 게시판 타입 + 정렬 조건으로 페이징된 게시글 조회
    
    Page<BbsDto> searchPosts(String searchType, String bbstitle, String bbscontent, String memberName, BoardType type, Pageable pageable); // 게시판 타입 + 검색 조건으로 페이징된 게시글 조회
    
    QandADto saveQna(Long bbsId, QandADto dto, String requesterAdminId); // QnA 답변 저장 (관리자만 가능)
    
    QandADto getQna(Long bbsId);  // 특정 게시글에 대한 QnA 답변 조회
    
    void deleteQna(Long qnaId, Long adminId); //특정 게시글의 답변 삭제
    
    QandADto updateQna(Long qnaId, QandADto dto); // QnA 답변 수정
    
    List<ImageBbsDto> getImageBbsList(Long bbsId); // 특정 게시글에 등록된 이미지 리스트 조회
    
    ImageBbsDto updateImage(Long imageId, ImageBbsDto dto, MultipartFile newFile); // 이미지 수정 (예: alt 텍스트나 순서 등 정보 변경)

    List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType); // 파일 업로드 리스트 저장 (첨부파일 등록)
    
    List<FileUpLoadDto> getFilesByBbs(Long bbsId); // 특정 게시글의 첨부파일 리스트 조회
    
    FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto, MultipartFile newFile); // 첨부파일 정보 수정
    
    void deleteFileById(Long fileId);
    
    FileUpLoadDto getFileById(Long fileId);

}

