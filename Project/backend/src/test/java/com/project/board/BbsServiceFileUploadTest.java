package com.project.board;

import com.project.board.dto.BbsDto;
import com.project.member.entity.MemberEntity;
import com.project.board.BoardType;
import com.project.board.repository.BbsRepository;
import com.project.member.repository.MemberRepository;
import com.project.board.service.BbsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BbsServiceFileUploadTest {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BbsRepository bbsRepository;

    @Test
    @DisplayName("공지사항 + 실제 파일 업로드 통합 테스트")
    public void createNoticePostWithRealFile() throws Exception {
        // 1. 회원 조회
        MemberEntity member = memberRepository.findById(3L)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 2. 실제 파일 불러오기
        File realFile = new File("src/test/resources/testfiles/test.pdf");
        FileInputStream fis = new FileInputStream(realFile);
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",
            realFile.getName(),
            "application/pdf",
            fis
        );

        // 3. 게시글 DTO 구성
        BbsDto dto = BbsDto.builder()
                .bbsTitle("실제 파일 업로드 공지사항")
                .bbsContent("본문에 파일 링크가 추가되어야 합니다.")
                .registDate(LocalDateTime.now())
                .revisionDate(null)
                .delDate(null)
                .viewers(0)
                .bulletinType(BoardType.NORMAL) // 공지사항
                .memberNum(member.getMemberNum())
                .build();

        // 4. 서비스 호출 (관리자 ID 전달)
        BbsDto saved = bbsService.createBbsWithFiles(dto, null, 1L, List.of(multipartFile));

        // 5. 결과 확인
        assertThat(saved.getBulletinNum()).isNotNull();
        assertThat(saved.getBbsTitle()).contains("실제 파일 업로드");
        assertThat(saved.getBbsContent()).contains(realFile.getName());

        System.out.println("등록된 게시글 번호: " + saved.getBulletinNum());
        System.out.println("본문 내용: " + saved.getBbsContent());
    }
}
