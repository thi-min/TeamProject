package com.project.board.service;

import com.project.board.BoardType;
import com.project.board.dto.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BbsService {
    BbsDto createBbs(BbsDto dto, Long requesterMemberNum, Long requesterAdminId);
    BbsDto updateBbs(Long id, BbsDto dto, Long memberNum);
    void deleteBbs(Long id, Long requesterMemberNum, Long requesterAdminId);
    void deleteBbs(List<Long> ids, Long requesterAdminId, Long requesterMemberNum);
    BbsDto getBbs(Long id);
    List<BbsDto> getAllByType(BoardType type);
    Page<BbsDto> getPagedPosts(BoardType type, String sort, Pageable pageable);
    Page<BbsDto> searchPosts(String searchType, String keyword, BoardType type, Pageable pageable);

    QandADto saveQna(Long bbsId, QandADto dto, Long requesterAdminId);
    QandADto getQna(Long bbsId);
    void deleteQna(Long qnaId);
    QandADto updateQna(Long qnaId, QandADto dto);

    List<ImageBbsDto> saveImageFileList(Long bbsId, List<MultipartFile> files);
    List<ImageBbsDto> getImageBbsList(Long bbsId);
    void deleteImage(Long imageId);
    void deleteImages(List<Long> imageIds);
    ImageBbsDto updateImage(Long imageId, ImageBbsDto dto);

    List<FileUpLoadDto> saveFileList(Long bbsId, List<MultipartFile> files, BoardType boardType);
    List<FileUpLoadDto> getFilesByBbs(Long bbsId);
    void deleteFile(Long fileId);
    FileUpLoadDto updateFile(Long fileId, FileUpLoadDto dto);
}

