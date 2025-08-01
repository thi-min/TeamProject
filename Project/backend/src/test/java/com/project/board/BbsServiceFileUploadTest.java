package com.project.board;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.board.dto.BbsDto;
import com.project.board.dto.QandADto;
import com.project.board.entity.BbsEntity;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("공지사항 작성 + 파일 첨부 + 본문 삽입 검증")
    void testCreateNoticeBoardByAdmin_withFileAndContentInjection() throws Exception {
        // 1. 관리자 엔티티 준비
        Long fakeAdminId = 1L;
        AdminEntity admin = adminRepository.findById(fakeAdminId).orElseGet(() -> {
            AdminEntity newAdmin = AdminEntity.builder()
                    .adminId("fakeAdminId")
                    .adminName("테스트관리자")
                    .adminEmail("admin@test.com")
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

        // 3. 테스트 파일 생성 (JPEG 이미지)
        Path filePath = Path.of("D:/temp/test.jpg"); // 존재하는 테스트 이미지 필요
        byte[] fileBytes = Files.readAllBytes(filePath);

        MockMultipartFile mockFile = new MockMultipartFile(
                "files",
                "test.jpg",
                "image/jpeg",
                fileBytes
        );

        List<MultipartFile> fileList = List.of(mockFile);

        // 4. 서비스 호출
        BbsDto saved = bbsService.createBbsWithFiles(dto, null, fakeAdminId, fileList);

        // 5. 본문 삽입 확인
        String updatedContent = saved.getBbsContent();
        assertNotNull(updatedContent);
        assertTrue(updatedContent.contains("<img src=\"/uploads/"));  // 이미지 삽입 확인
        assertTrue(updatedContent.contains("alt=\"test.jpg\""));      // 파일 이름이 alt로 들어가는지 확인
    }
}
