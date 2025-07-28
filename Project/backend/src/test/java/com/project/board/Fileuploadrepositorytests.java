package com.project.board;

import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class Fileuploadrepositorytests {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private MemberRepository memberRepository;


    private final String TEST_FILE_NAME = "test.pdf";
    private final String TEST_FILE_PATH = "C:/upload"; // 테스트용 파일 실제 경로

    @Test
    @DisplayName("게시글 작성 후 첨부파일 저장 테스트")
    public void testCreatePostAndUploadFileSeparately() throws IOException {
        // 1. 실제 존재하는 회원 조회
        MemberEntity member = memberRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

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

        // 3. 게시글 저장 → 저장된 게시글 ID 받아오기
        BbsDto savedBbs = bbsService.createBbs(dto, member.getMemberNum(), null);
        Long savedBbsId = savedBbs.getBulletinNum();

        // 4. 파일 준비 (실제 파일 기반 MockMultipartFile 생성)
        FileInputStream fis = new FileInputStream(TEST_FILE_PATH);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                TEST_FILE_NAME,
                "application/pdf",
                fis
        );

        // 5. 파일 저장 서비스 호출
        List<FileUpLoadDto> savedFiles = bbsService.saveFileList(
                savedBbsId,
                List.of(multipartFile),
                BoardType.FAQ
        );

        // 6. 검증
        assertNotNull(savedFiles);
        assertEquals(1, savedFiles.size());
        assertEquals("pdf", savedFiles.get(0).getExtension());

        System.out.println("첨부파일 저장 성공: " + savedFiles.get(0).getOriginalName());
    }
}
