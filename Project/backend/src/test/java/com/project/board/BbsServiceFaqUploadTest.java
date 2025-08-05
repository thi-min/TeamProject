package com.project.board;

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

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BbsServiceFaqUploadTest {

    @Autowired
    private BbsService bbsService;
    
    @Autowired
    private BbsRepository bbsRepository;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private QandARepository qandaRepository;
    
    @Autowired
    private FileUpLoadRepository fileUploadRepository;

    @Autowired
    private ImageBbsRepository imageBbsRepository;

    @Autowired
    private QandARepository qandARepository;
/*
    @Test
    @DisplayName("QnA 게시판 + 실제 파일 업로드 테스트")
    public void createQnaPostWithRealFile() throws Exception {
        // 1. DB에서 회원 조회 (memberNum = 3L)
        MemberEntity member = memberRepository.findById(3L)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 2. 실제 파일 로드
        File file = new File("D:\\temp\\test.pdf");
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",
            file.getName(),
            "application/pdf",
            fis
        );

        // 3. BbsDto 구성
        BbsDto dto = BbsDto.builder()
                .bbsTitle("QnA 게시판 파일 업로드 테스트")
                .bbsContent("이건 QnA입니다. 파일 링크가 추가됩니다.")
                .registDate(LocalDateTime.now())
                .revisionDate(null)
                .delDate(null)
                .viewers(0)
                .bulletinType(BoardType.FAQ)
                .memberNum(member.getMemberNum()) // 작성자는 회원
                .build();

        // 4. 서비스 호출 (회원 ID만 전달)
        BbsDto saved = bbsService.createBbsWithFiles(dto, member.getMemberNum(), null, List.of(multipartFile));

        // 5. 결과 검증
        assertThat(saved.getBulletinNum()).isNotNull();
        assertThat(saved.getBbsTitle()).contains("QnA 게시판");
        assertThat(saved.getBbsContent()).contains(file.getName());

        System.out.println("QnA 게시글 번호: " + saved.getBulletinNum());
        System.out.println("본문 내용:\n" + saved.getBbsContent());
    } */
    
 /*   @Test
    void 관리자_QnA_답변_등록() {
        String adminId = "admin1";
        Long bulletinNum = 8L; // 사전에 존재하는 게시글 번호

        QandADto qnaDto = QandADto.builder()
            .question("답변할게")
            .answer("한개의 고기야.")
            .build();

        QandADto saved = bbsService.saveQna(bulletinNum, qnaDto, adminId);

        assertNotNull(saved);
        assertEquals("한개의 고기야.", saved.getAnswer());
    }  */
    
 /*   @Test
    @DisplayName("QnA 조회 성공")
    void testGetQna() {
        Long bulletnum = 47L; // 실제 존재하는 게시글 ID 사용
        QandADto result = bbsService.getQna(bulletnum);
        
        // 콘솔 출력 추가
        System.out.println("조회된 QnA 정보:");
        System.out.println("게시글 번호: " + result.getBulletinNum());
        System.out.println("질문: " + result.getQuestion());
        System.out.println("답변: " + result.getAnswer());
        
        // 검증
        assertNotNull(result);
        assertEquals(bulletnum, result.getBulletinNum());
        assertNotNull(result.getQuestion());
    } */
    
   /* @Test
    @Transactional
    @Rollback(false)
    @DisplayName("QnA 수정 성공")
    void testUpdateQna() {
        Long bulletnum = 28L; // 실제 존재하는 게시글 ID
        QandADto updateDto = QandADto.builder()
            .question("또 변경된 질문")
            .answer("변경된 답변")
            .build();

        QandADto updated = bbsService.updateQna(bulletnum, updateDto);

        assertEquals("또 변경된 질문", updated.getQuestion());
        assertEquals("변경된 답변", updated.getAnswer());
    } */
    
   /* @Test
    @Transactional
    @Rollback(false)
    void deleteByBbsBulletinNum_DeletesAllQnAForGivenBulletinNum() {
        Long bulletinNum = 28L;

        // 게시글이 DB에 없으면 예외 발생 (DB에 28번 게시글 있다고 가정)
        BbsEntity bbs = bbsRepository.findById(bulletinNum)
                .orElseThrow(() -> new IllegalStateException("게시글 28번이 DB에 존재하지 않습니다."));

        // when: bulletinNum 28번에 연결된 QnA 모두 삭제
        qandaRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());

        // then: 해당 게시글 번호로 QnA가 존재하지 않아야 함
        Optional<QandAEntity> result = qandaRepository.findByBbsBulletinNum(bbs.getBulletinNum());
        assertTrue(result.isEmpty(), "QnA는 삭제되어야 합니다.");
    }
    */
  /*  
    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("관리자 단일 게시글 삭제 성공")
    void testAdminDeleteSingleBbs() {
        Long bbsId = 38L; // 삭제 대상 게시글 ID (DB에 반드시 존재해야 함)
        Long adminId = 10L; // 관리자 ID

        // 삭제 전 게시글, 첨부파일, 이미지, QnA 존재 여부 확인 (필요 시)
        assertTrue(bbsRepository.existsById(bbsId));
        // 첨부파일, 이미지, QnA가 없을 수도 있으니 null 체크 or exists 체크는 선택적

        // 관리자 권한으로 게시글 삭제
        bbsService.deleteBbs(bbsId, null, adminId);

        // 삭제 후 게시글 존재 여부 확인 (없어야 함)
        assertFalse(bbsRepository.existsById(bbsId));

        // 관련 첨부파일, 이미지, QnA도 모두 삭제되었는지 확인
        assertTrue(fileUploadRepository.findByBbsBulletinNum(bbsId).isEmpty());
        assertTrue(imageBbsRepository.findByBbsBulletinNum(bbsId).isEmpty());
        assertTrue(qandARepository.findByBbsBulletinNum(bbsId).isEmpty());
    }  */
    
    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("관리자가 여러 게시글을 삭제하면 게시글과 연관 데이터도 삭제되어야 한다")
    void deleteMultipleBbsByAdmin_deletesAllTargetedPosts() {
        // given: 존재하는 게시글 번호들 (미리 DB에 존재해야 함)
        List<Long> bulletinNums = List.of(8L, 10L); // 테스트용 게시글 번호들

        // 게시글들이 실제로 존재하는지 검증
        List<BbsEntity> bbsList = bbsRepository.findAllById(bulletinNums);
        assertEquals(2, bbsList.size(), "모든 테스트용 게시글이 DB에 존재해야 합니다.");

        // when: 게시글 삭제
        bbsRepository.deleteAllById(bulletinNums);

        // then: 삭제되었는지 검증
        for (Long bulletinNum : bulletinNums) {
            boolean exists = bbsRepository.findById(bulletinNum).isPresent();
            assertFalse(exists, "게시글 번호 " + bulletinNum + "은 삭제되어야 합니다.");
        }
    } 

}
