package com.project.board;

import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.entity.BbsEntity;
import com.project.board.entity.FileUpLoadEntity;
import com.project.member.entity.MemberEntity;
import com.project.board.repository.BbsRepository;
import com.project.board.repository.FileUpLoadRepository;
import com.project.member.repository.MemberRepository;
import com.project.board.service.BbsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class Fileuploadrepositorytests {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private FileUpLoadRepository fileUpLoadRepository;

    @Autowired
    private BbsRepository bbsRepository;

   // private final String TEST_FILE_NAME = "test.pptx";
  //  private final String TEST_FILE_PATH = "D:\\temp\\" + TEST_FILE_NAME;

   /* @Test
    @DisplayName("게시글 작성 후 첨부파일 저장 테스트")
    public void testCreatePostAndUploadFileSeparately() throws IOException {
        // 1. 실제 존재하는 회원 조회
        MemberEntity member = memberRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("회원 ID 3번이 존재하지 않습니다."));

        // 2. 게시글 DTO 생성
        BbsDto dto = BbsDto.builder()
                .memberNum(member.getMemberNum())
                .memberName(member.getMemberName())
                .bbsTitle("파일 분리 업로드 테스트")
                .bbsContent("게시글 저장 후 첨부파일 따로 저장 테스트")
                .registDate(LocalDateTime.now())
                .revisionDate(null)
                .delDate(null)
                .viewers(0)
                .bulletinType(BoardType.FAQ)
                .build();

        // 3. 게시글 저장
        BbsDto savedBbs = bbsService.createBbs(dto, member.getMemberNum(), null);
        Long savedBbsId = savedBbs.getBulletinNum();
        assertNotNull(savedBbsId, "게시글 저장에 실패했습니다.");

        // 4. 파일 존재 확인 및 읽기
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists(), "테스트 파일이 존재하지 않습니다: " + TEST_FILE_PATH);

        try (FileInputStream fis = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    file.getName(),
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation", // pptx MIME
                    fis
            );

            // 5. 파일 저장 서비스 호출
            List<FileUpLoadDto> savedFiles = bbsService.saveFileList(
                    savedBbsId,
                    List.of(multipartFile),
                    BoardType.FAQ
            );

            // 6. 검증
            assertNotNull(savedFiles, "파일 리스트가 null입니다.");
            assertEquals(1, savedFiles.size(), "저장된 파일 개수가 1개가 아닙니다.");
            assertEquals("pptx", savedFiles.get(0).getExtension(), "파일 확장자가 일치하지 않습니다.");

            System.out.println("첨부파일 저장 성공: " + savedFiles.get(0).getOriginalName());

        } catch (IOException e) {
            fail("파일 읽기 또는 저장 중 오류 발생: " + e.getMessage());
        }
    } */
    
  
     

        @Test
        @Transactional
        @Rollback(false)
        @DisplayName("게시글 번호로 첨부파일 모두 삭제 테스트")   
        void deleteByBbsBulletinNum_DeletesAllFiles() {
            // 1. 테스트용 게시글 준비 (존재하는 게시글 번호 사용)
            Long bulletinNum = 35L; // 실제 DB에 있는 게시글 번호로 변경하세요

            BbsEntity bbs = bbsRepository.findById(bulletinNum)
                    .orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다"));

            // 2. 첨부파일이 존재하는지 미리 확인
            List<FileUpLoadEntity> filesBeforeDelete = fileUpLoadRepository.findAll()
                    .stream()
                    .filter(f -> f.getBbs().getBulletinNum().equals(bulletinNum))
                    .toList();

            assertFalse(filesBeforeDelete.isEmpty(), "테스트 대상 게시글에 첨부파일이 없습니다.");

            // 3. 삭제 실행
            fileUpLoadRepository.deleteByBbsBulletinNum(bulletinNum);

            // 4. 삭제 후 첨부파일 존재 여부 확인
            List<FileUpLoadEntity> filesAfterDelete = fileUpLoadRepository.findAll()
                    .stream()
                    .filter(f -> f.getBbs().getBulletinNum().equals(bulletinNum))
                    .toList();

            assertTrue(filesAfterDelete.isEmpty(), "첨부파일이 모두 삭제되어야 합니다.");
        }
    }


