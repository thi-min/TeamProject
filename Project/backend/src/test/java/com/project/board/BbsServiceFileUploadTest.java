package com.project.board;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.dto.QandADto;
import com.project.board.entity.BbsEntity;
import com.project.board.entity.FileUpLoadEntity;
import com.project.board.entity.QandAEntity;
import com.project.board.repository.BbsRepository;
import com.project.board.repository.FileUpLoadRepository;
import com.project.board.repository.ImageBbsRepository;
import com.project.board.repository.QandARepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.board.service.BbsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BbsServiceFileUploadTest {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private FileUpLoadRepository fileuploadrepository;
    
    @Autowired
    private BbsRepository bbsRepository;
    
    @Autowired
    private ImageBbsRepository imagebbsRepository;


   /* @Test
    @Transactional
    @Rollback(false)
    @DisplayName("공지사항 작성 + 파일 첨부 + 본문 삽입 검증")
    void testCreateNoticeBoardByAdmin_withFileAndContentInjection() throws Exception {
        // 1. 관리자 엔티티 준비
        Long fakeAdminId = 6L;
        AdminEntity admin = adminRepository.findById(fakeAdminId).orElseGet(() -> {
            AdminEntity newAdmin = AdminEntity.builder()
                    .adminId("fakeAdminIds")
                    .adminName("테스트관리자임")
                    .adminEmail("admin1@test.com")
                    .adminPw("encoded_password")
                    .adminPhone("010-1234-5678") 
                    .registDate(LocalDateTime.now())
                    .connectData(LocalDateTime.now())
                    .build();

            return adminRepository.save(newAdmin);
        });

        // 2. 게시글 DTO 생성
        BbsDto dto = BbsDto.builder()
                .bbsTitle("파일 삽입 본문 테스트")
                .bbsContent("본문 시작")
                .registDate(LocalDateTime.now())
                .revisionDate(null)
                .delDate(null)
                .viewers(0)
                .bulletinType(BoardType.NORMAL) // NORMAL → 본문 수정 대상
                .memberNum(null)
                .build();

        // 3-1. 첫 번째 테스트 파일 (test.pdf)
        Path filePath1 = Path.of("D:/temp/test.pdf");
        byte[] fileBytes1 = Files.readAllBytes(filePath1);
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "files",
                "test.pdf",
                "application/pdf",  // 수정: pdf는 application/pdf
                fileBytes1
        );

        // 3-2. 두 번째 테스트 파일 (test1.jpg)
        Path filePath2 = Path.of("D:/temp/test1.jpg");
        byte[] fileBytes2 = Files.readAllBytes(filePath2);
        MockMultipartFile mockFile2 = new MockMultipartFile(
                "files",
                "test1.jpg",
                "image/jpeg",
                fileBytes2
        );

        // 두 개 파일을 리스트에 담기
        List<MultipartFile> fileList = List.of(mockFile1, mockFile2);

        // 4. 서비스 최상위 메서드 호출 (createBbs)
        BbsDto saved = bbsService.createBbs(dto, null, fakeAdminId, fileList);

        // 5. 본문 삽입 확인
        String updatedContent = saved.getBbsContent();
        assertNotNull(updatedContent);
        assertTrue(updatedContent.contains("<img src=\"/uploads/"));  // 이미지 삽입 확인
        assertTrue(updatedContent.contains("alt=\"test1.jpg\""));      // 이미지 파일 이름 alt 속성 확인
        assertTrue(updatedContent.contains("test.pdf"));               // pdf 링크 포함 확인
    } */
    
 /*   @Test
    @DisplayName("파일 업데이트 테스트 - test1.jpg → test.pptx")
    @Transactional
    @Rollback(false)
    void testUpdateFileReplacePdfWithPptx() throws Exception {
        Long bulletinNum = 74L;

        List<FileUpLoadEntity> attachments = fileuploadrepository.findByBbsBulletinNum(bulletinNum);
        assertFalse(attachments.isEmpty(), "기존 첨부파일이 있어야 합니다.");

        Optional<FileUpLoadEntity> fileOpt = attachments.stream()
                .filter(f -> f.getOriginalName().equals("test.pptx"))
                .findFirst();

        assertTrue(fileOpt.isPresent(), "test1.jpg 파일이 존재해야 합니다.");
        FileUpLoadEntity existingFile = fileOpt.get();

        // 새로운 파일 준비
        File newFile = new File("D:/temp/manager.pdf");
        byte[] fileBytes = Files.readAllBytes(newFile.toPath());

        MockMultipartFile newMockFile = new MockMultipartFile(
                "file",
                newFile.getName(),
                Files.probeContentType(newFile.toPath()),
                fileBytes
        );

        // 확장자 및 저장 이름
        String extension = getExtension(newMockFile.getOriginalFilename());
        String savedName = UUID.randomUUID().toString() + "." + extension;

        FileUpLoadDto updateDto = FileUpLoadDto.dtoBuilder()
                .fileNum(existingFile.getFilenum())
                .originalName(newMockFile.getOriginalFilename())
                .savedName(savedName)
                .path("/uploads/" + savedName)
                .size(newMockFile.getSize())
                .extension(extension)
                .build();

        // 업데이트 실행
        FileUpLoadDto updated = bbsService.updateFile(existingFile.getFilenum(), updateDto, newMockFile);

        // 검증
        assertEquals("manager.pdf", updated.getOriginalName(), "파일명이 test.pptx로 변경되어야 합니다.");
        assertEquals("pdf", updated.getExtension(), "확장자는 pptx여야 합니다.");

        // 파일 실제 존재 확인
        Path savedPath = Path.of("C:/photo", updated.getSavedName());
        assertTrue(Files.exists(savedPath), "파일이 실제로 C:/photo 경로에 존재해야 합니다.");
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    } */
    
    
  /*
    @Test
    @Transactional
    @Rollback(false) // 삭제 결과를 실제로 반영하여 파일 삭제 여부까지 검증
    void 게시글삭제_작성자권한으로_관련데이터_및_파일_삭제() throws Exception {
        // given
        Long bulletinNum = 76L;     // 삭제 대상 게시글 번호
        Long memberId = 23L;        // 게시글 작성자 ID
        Long adminId = null;       // 관리자 아님

        // when
        bbsService.deleteBbs(bulletinNum, memberId, adminId); // 작성자 권한으로 삭제 수행

        // then
        // 1. 게시글 삭제 확인
        assertFalse(bbsRepository.findById(bulletinNum).isPresent(), "게시글이 삭제되어야 함");

        // 2. 첨부파일 삭제 확인
        assertTrue(fileuploadrepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "첨부파일이 삭제되어야 함");

        // 3. 이미지 삭제 확인
        assertTrue(imagebbsRepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "이미지가 삭제되어야 함");

  
    } */
  /*  
    @Test
    @Transactional
    @Rollback(false) // 실제 파일 삭제 여부 검증
    void 게시글여러개삭제_작성자권한으로_관련데이터_및_파일_삭제() {
        // given
        List<Long> bulletinNums = List.of(57L, 60L); // 삭제할 게시글 번호들 (DB에 존재해야 함)
        Long memberId = 9L; // 게시글 작성자 ID
        Long adminId = null; // 관리자 아님

        for (Long bulletinNum : bulletinNums) {
            // when
            bbsService.deleteBbs(bulletinNum, memberId, adminId); // 작성자 권한으로 삭제 수행

            // then
            // 1. 게시글 삭제 확인
            assertFalse(bbsRepository.findById(bulletinNum).isPresent(), "게시글이 삭제되어야 함: " + bulletinNum);

            // 2. 첨부파일 삭제 확인
            assertTrue(fileuploadrepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "첨부파일이 삭제되어야 함: " + bulletinNum);

            // 3. 이미지 삭제 확인
            assertTrue(imagebbsRepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "이미지가 삭제되어야 함: " + bulletinNum);
        }
    }
*/

    
    @Test
    @Transactional
    @Rollback(false) // 실제 파일 삭제 여부 검증
    void 게시글여러개삭제_작성자권한으로_관련데이터_및_파일_삭제() {
        // given
        List<Long> bulletinNums = List.of(56L, 78L); // 삭제할 게시글 번호들 (DB에 존재해야 함)
        Long memberId = null; // 게시글 작성자 ID
        Long adminId = 6L; // 관리자 아님

        for (Long bulletinNum : bulletinNums) {
            // when
            bbsService.deleteBbs(bulletinNum, memberId, adminId); // 작성자 권한으로 삭제 수행

            // then
            // 1. 게시글 삭제 확인
            assertFalse(bbsRepository.findById(bulletinNum).isPresent(), "게시글이 삭제되어야 함: " + bulletinNum);

            // 2. 첨부파일 삭제 확인
            assertTrue(fileuploadrepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "첨부파일이 삭제되어야 함: " + bulletinNum);

            // 3. 이미지 삭제 확인
            assertTrue(imagebbsRepository.findByBbsBulletinNum(bulletinNum).isEmpty(), "이미지가 삭제되어야 함: " + bulletinNum);
        }
    }

}

