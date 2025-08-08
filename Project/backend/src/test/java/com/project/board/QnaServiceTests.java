package com.project.board;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.board.dto.BbsDto;
import com.project.board.dto.QandADto;
import com.project.board.entity.BbsEntity;
import com.project.board.entity.QandAEntity;
import com.project.board.repository.BbsRepository;
import com.project.board.repository.QandARepository;
import com.project.board.service.BbsService;
import com.project.board.BoardType;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("게시글 + QnA 작성 테스트")
class QnaServiceTests {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private QandARepository qandaRepository;
    
    @Autowired
    private BbsRepository bbsRepository;
    

   /* @Test
    void 회원이_작성한_게시글에_관리자가_QnA_답변() {
    	MemberEntity member = memberRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    	
        // 2. 관리자 저장 (직접 임의로 저장)
        AdminEntity admin = AdminEntity.builder()
        		.adminId("admin1")
                .adminPw("adminpw")
                .adminName("관리자1")
                .adminEmail("admin@email.com")
                .adminPhone("010-9999-0000")
                .member(member)  // 필요 시 넣어줘야 함 (nullable 여부 확인)
                .registDate(LocalDateTime.now())
                .connectData(LocalDateTime.now())  // ✅ 이 줄 꼭 추가
                .build();
        admin = adminRepository.save(admin); 

        // 3. 게시글 DTO 생성
    			BbsDto dto = BbsDto.builder()
                .memberNum(member.getMemberNum())   // 방금 저장한 회원 번호 사용
                .memberName(member.getMemberName()) // 회원 이름
                .bbsTitle("궁금한거 질문")
                .bbsContent("궁금한게 있어.")
                .registDate(LocalDateTime.now())
                .revisionDate(null)  // 수정 전
                .delDate(null)       // 삭제 전
                .viewers(0)
                .bulletinType(BoardType.FAQ)  // 예: FAQ 게시판
                .build();

        // 4. 게시글 저장
        BbsDto savedBbs = bbsService.createBbs(dto, member.getMemberNum(), null);

        // 5. QnA DTO 생성
        QandADto qnaDto = QandADto.builder()
                .question("질문이 있네.")
                .answer("관리자가 답변합니다.")  // 실제 저장될 답변
                .build();

        // 6. QnA 저장 (관리자의 ID 사용)
        QandADto savedQna = bbsService.saveQna(savedBbs.getBulletinNum(), qnaDto, admin.getAdminId());

        // 7. 검증
        assertNotNull(savedQna);
        assertEquals(savedBbs.getBulletinNum(), savedQna.getBulletinNum());
        assertEquals("관리자가 답변합니다.", savedQna.getAnswer());
    } */
    
   /* @Test
    void 회원_존재여부_확인() {
        MemberEntity member = memberRepository.findById(3L)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        
        assertNotNull(member);
        assertEquals(3L, member.getMemberNum());
    } */
    
  /*  @Test
    void 회원이_게시글_작성() {
        MemberEntity member = memberRepository.findById(3L)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        BbsDto dto = BbsDto.builder()
            .memberNum(member.getMemberNum())
            .memberName(member.getMemberName())
            .bbsTitle("인생의 심각한 일이 있어요")
            .bbsContent("지구를 절반으로 쪼개는 건 가능한가요?")
            .registDate(LocalDateTime.now())
            .bulletinType(BoardType.FAQ)
            .viewers(0)
            .build();

        BbsDto saved = bbsService.createBbs(dto, member.getMemberNum(), null);

        assertNotNull(saved);
        assertEquals("인생의 심각한 일이 있어요", saved.getBbsTitle());
    }*/

   /* @Test
    void 관리자_QnA_답변_등록() {
        String adminId = "admin1";
        Long bulletinNum = 28L; // 사전에 존재하는 게시글 번호

        QandADto qnaDto = QandADto.builder()
            .question("답변드릴게요?")
            .answer("니가 쪼개봐.")
            .build();

        QandADto saved = bbsService.saveQna(bulletinNum, qnaDto, adminId);

        assertNotNull(saved);
        assertEquals("니가 쪼개봐.", saved.getAnswer());
    } */

/*    @Test
    void QnA_게시글_번호_매칭_검증() {
        Long bulletinNum = 28L;

        Optional<QandAEntity> qnaOpt = qandaRepository.findById(bulletinNum);

        assertTrue(qnaOpt.isPresent());
        assertEquals(bulletinNum, qnaOpt.get().getBbs().getBulletinNum());
    } */
    
   /* @Test
    void findByBbsBulletinNum_exists() {
        Long existingBulletinNum = 25L; // DB에 실제 존재하는 게시글 번호를 넣어주세요

        Optional<QandAEntity> result = qandaRepository.findByBbsBulletinNum(existingBulletinNum);

        assertTrue(result.isPresent(), "QnA 엔티티가 존재해야 합니다.");
        assertEquals(existingBulletinNum, result.get().getBbs().getBulletinNum());
    }
 */
    @Test
    @Transactional
    void deleteByBbsBulletinNum_DeletesAllQnAForGivenBulletinNum() {
        Long bulletinNum = 26L;

        // 게시글이 DB에 없으면 예외 발생 (DB에 25번 게시글 있다고 가정)
        BbsEntity bbs = bbsRepository.findById(bulletinNum)
                .orElseThrow(() -> new IllegalStateException("게시글 25번이 DB에 존재하지 않습니다."));

        // when: bulletinNum 25번에 연결된 QnA 모두 삭제
        qandaRepository.deleteByBbsBulletinNum(bbs.getBulletinNum());

        // then: 해당 게시글 번호로 QnA가 존재하지 않아야 함
        Optional<QandAEntity> result = qandaRepository.findByBbsBulletinNum(bbs.getBulletinNum());
        assertTrue(result.isEmpty(), "QnA는 삭제되어야 합니다.");
    }
}
