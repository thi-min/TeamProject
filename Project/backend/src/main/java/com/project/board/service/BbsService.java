package com.project.board.service;

import com.project.board.BoardType;
import com.project.board.dto.*;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface BbsService {
	
	//requesterAdminId Long > String 타입 변경
    BbsDto createBbs(BbsDto dto, Long requesterMemberNum, String requesterAdminId, List<MultipartFile> files, List<String> insertOptions, List<String> isRepresentativeList); // 게시글 생성 (회원 또는 관리자)
    
    BbsDto createPotoBbs( BbsDto dto, Long requesterMemberNum, List<MultipartFile> files, List<String> isRepresentativeList);
    
    BbsDto createBbsWithFiles(BbsDto dto, Long requesterMemberNum, String requesterAdminId, List<MultipartFile> files,  List<String> insertOptions); // 생성된 게시글 + 파일첨부 (회원 또는 관리자)
    
    BbsDto updateBbs(Long id, BbsDto dto, Long userId, String adminId, List<MultipartFile> newFiles, List<Long> deleteFileIds, boolean isAdmin, List<String> insertOptions); // 게시글 수정 (작성자 본인만 가능)
    //BbsDto updateBbsAdmin(Long id, BbsDto dto, String adminId, List<MultipartFile> newFiles, List<Long> deleteFileIds, boolean isAdmin, List<String> insertOptions); // 게시글 수정 (작성자 본인만 가능)
  //requesterAdminId Long > String 타입 변경
    void deleteBbs(Long id, Long requesterMemberNum, String requesterAdminId);  // 게시글 단건 삭제 (작성자 본인 또는 관리자)
    
  //requesterAdminId Long > String 타입 변경
    void deleteBbsMultiple(List<Long> ids, Long requesterMemberNum, String requesterAdminId); //게시글 복수건 삭제(관리자)
    
    BbsDto getBbs(Long id); // 게시글 단건 조회
    
    List<BbsDto> getAllByType(BoardType type); // 특정 게시판 타입으로 모든 게시글 리스트 조회
    
    Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable); // 게시판 타입 + 정렬 조건으로 페이징된 게시글 조회
    
    Page<BbsDto> searchPosts(String searchType, String bbstitle, String bbscontent, String memberName, BoardType type, Pageable pageable); // 게시판 타입 + 검색 조건으로 페이징된 게시글 조회
    
    QandADto saveQna(Long bbsId, QandADto dto, String requesterAdminId); // QnA 답변 저장 (관리자만 가능)
    
    QandADto getQna(Long bbsId);  // 특정 게시글에 대한 QnA 답변 조회
    
    void deleteQna(Long qnaId, Long adminId); //특정 게시글의 답변 삭제
    
    QandADto updateQna(Long qnaId, QandADto dto); // QnA 답변 수정
    
    List<ImageBbsDto> getImageBbsList(Long bbsId); // 특정 게시글에 등록된 이미지 리스트 조회
    
    BbsDto updatePotoBbs(Long bulletinNum, BbsDto dto, List<MultipartFile> newFiles, List<Long> representativeFileIds, List<Long> deletedFileIds,List<Long> overwriteFileIds, Long requesterMemberNum);

    List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType); // 파일 업로드 리스트 저장 (첨부파일 등록)
    
    List<FileUpLoadDto> getFilesByBbs(Long bbsId); // 특정 게시글의 첨부파일 리스트 조회
    
    FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto, MultipartFile newFile); // 첨부파일 정보 수정
    
    void deleteFileById(Long fileId);
    
    FileUpLoadDto getFileById(Long fileId);
    
    ImageBbsDto getRepresentativeImage(Long bulletinNum);
    
    // FAQ 게시글 리스트 조회
    Map<String, Object> getBbsList(BoardType type, int page, int size, String bbstitle, String memberName, String bbscontent);

	List<BbsSimpleResponseDto> getLatestNormalPosts();

	

}

