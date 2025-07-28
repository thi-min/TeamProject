package com.project.board;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.board.dto.BbsDto;
import com.project.board.entity.BbsEntity;
import com.project.board.repository.BbsRepository;
import com.project.board.service.BbsService;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.board.BoardType;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;


@SpringBootTest
public class Bbsrepositorytests {
	@Autowired
	private BbsRepository bbsRepository;

    @Autowired
    private BbsService bbsService;

    @Autowired
    private MemberRepository memberRepository;
/*
    @Test
    @DisplayName("회원 생성 및 게시글 생성 테스트")
    public void testCreateBbs() {
        // 1. 회원 생성 및 저장
    		MemberEntity member = MemberEntity.builder()
                .memberId("testuser1@example.com")
                .memberPw("password1234")
                .memberName("테스트유저2")
                .memberBirth(LocalDate.of(1991, 2, 2))
                .memberAddress("서울 강북구")
                .memberDay(LocalDate.now())
                .memberLock(false)
                .memberPhone("010-1111-2222")
                .memberSex(MemberSex.Woman)
                .memberState(MemberState.ACTIVE)
                .snsYn(true)
                .build();

        member = memberRepository.save(member); // 저장 후 memberNum 생성됨

        // 2. 게시글 DTO 생성
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

        Long memberNum = dto.getMemberNum();
        Long adminId = null;  // 일반 회원 작성

        // 3. 게시글 생성 서비스 호출
        BbsDto result = bbsService.createBbs(dto, memberNum, adminId);

        // 4. 결과 출력 (or Assertions 추가)
        System.out.println("생성된 게시글 번호: " + result.getBulletinNum());
        System.out.println("제목: " + result.getBbsTitle());
        System.out.println("작성자: " + result.getMemberName()); 
    }*/
   
    
    /*@Test
    @DisplayName("기존 회원으로 게시글 작성 테스트")
    public void testCreateBbsByExistingMember() {
        // 1. DB에 있는 회원 조회 (memberNum = 1)
        MemberEntity member = memberRepository.findById(3L)
            .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 2. 게시글 DTO 생성
        BbsDto dto = BbsDto.builder()
                .memberNum(member.getMemberNum())   // DB에서 조회한 회원 번호
                .memberName(member.getMemberName()) // DB에서 조회한 회원 이름
                .bbsTitle("퍼펙트 웰던")
                .bbsContent("질문이있네.")
                .registDate(LocalDateTime.now())
                .revisionDate(null)
                .delDate(null)
                .viewers(0)
                .bulletinType(BoardType.FAQ)  // 예시로 FAQ 게시판
                .build();

        // 3. 게시글 생성 서비스 호출 (관리자 ID는 null, 회원이 작성하므로)
        BbsDto result = bbsService.createBbs(dto, member.getMemberNum(), null);

        // 4. 결과 출력
        System.out.println("생성된 게시글 번호: " + result.getBulletinNum());
        System.out.println("제목: " + result.getBbsTitle());
        System.out.println("작성자: " + result.getMemberName());
    }
*/
    	
   /* @Test
    @DisplayName("특정 게시판 타입 게시글 조회 테스트")
    public void testFindByBulletinType() {
        // 이미 DB에 데이터가 있다고 가정하고 바로 조회
        BoardType targetType = BoardType.FAQ;  // 조회할 게시판 타입 지정

        // 특정 게시판 타입 게시글 전체 조회
        List<BbsEntity> posts = bbsRepository.findByBulletinType(targetType);

        // 조회된 게시글 수 출력
        System.out.println(targetType + " 게시판 글 개수: " + posts.size());

        // 간단한 검증: 게시글이 1개 이상 존재해야 테스트 의미 있음
        assert posts.size() > 0;

        // 첫 게시글 제목 출력 (필요시)
        if (!posts.isEmpty()) {
            System.out.println("첫 게시글 제목: " + posts.get(1).getBbstitle());
        }
    } */
    
   /* @Test
    @DisplayName("특정 회원이 작성한 게시글 조회 테스트")
    public void testFindByMemberNum() {
        // 1. 조회할 회원 엔티티(또는 memberNum)를 DB에서 직접 조회하거나 미리 알고 있다고 가정
        Long targetMemberNum = 1L;  // 예: 조회할 회원 번호

        // 회원 엔티티 조회
        MemberEntity member = memberRepository.findById(targetMemberNum)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 2. 해당 회원이 작성한 게시글 리스트 조회
        List<BbsEntity> memberPosts = bbsRepository.findByMemberNum(member);

        // 3. 결과 출력 및 검증
        System.out.println("회원 번호 " + targetMemberNum + "가 작성한 게시글 수: " + memberPosts.size());

        assert memberPosts.size() > 0;  // 게시글이 최소 하나 이상 존재해야 테스트 의미 있음

        // 첫 게시글 제목 출력 (옵션)
        if (!memberPosts.isEmpty()) {
            System.out.println("첫 게시글 제목: " + memberPosts.get(0).getBbstitle());
        }
    } */
    
   /* @Test
    @DisplayName("10개 미만 게시글 페이지 조회 테스트")
    public void testFindByBulletinTypeWithLessThan10Posts() {
        // 1. 페이지 정보 세팅 (한 페이지에 10개)
        Pageable pageable = PageRequest.of(0, 10);

        // 2. 특정 게시판 타입의 게시글 조회 (글 수가 10개 미만인 상태라고 가정)
        Page<BbsEntity> page = bbsRepository.findByBulletinType(BoardType.FAQ, pageable);

        // 3. 조회 결과 개수 출력
        System.out.println("조회된 게시글 수: " + page.getContent().size());

        // 4. 개수가 10개보다 작거나 같음을 검증
        assertTrue(page.getContent().size() <= 10);

        // 5. 첫 게시글 제목 출력 (존재할 경우)
        if (!page.isEmpty()) {
            System.out.println("첫 게시글 제목: " + page.getContent().get(0).getBbstitle());
        }
    }
*/
    
   /* @Test
    @DisplayName("10개 이상 게시글 페이지 조회 테스트")
    public void testFindByBulletinTypeWithMoreThan10Posts() {
        // 1. 페이지 정보 세팅 (한 페이지에 10개)
        Pageable pageable = PageRequest.of(0, 10);

        // 2. 특정 게시판 타입의 게시글 조회 (게시글이 10개 이상 존재한다고 가정)
        Page<BbsEntity> page = bbsRepository.findByBulletinType(BoardType.FAQ, pageable);

        // 3. 조회된 게시글 수 출력
        System.out.println("조회된 게시글 수: " + page.getContent().size());

        // 4. 10개 이상 존재하는지 검증
        assertTrue(page.getTotalElements() >= 10, "게시글이 10개 이상이어야 합니다.");

        // 5. 첫 게시글 제목 출력 (있다면)
        if (!page.isEmpty()) {
            System.out.println("첫 게시글 제목: " + page.getContent().get(0).getBbstitle());
        }
    }*/
    
   /* @Test
    @DisplayName("제목 키워드 포함된 게시글이 10개 이상인 경우 페이징 조회 테스트")
    public void testFindByTitleKeywordWithMoreThan10Posts() {
        // 1. 검색할 키워드 지정 (예: "테스트")
        String bbstitle = "궁금한거  질문";

        // 2. 페이지 요청 설정: 첫 번째 페이지, 한 페이지당 10개
        Pageable pageable = PageRequest.of(0, 10);

        // 3. 키워드를 포함한 게시글 조회
        Page<BbsEntity> page = bbsRepository.findByBbstitleContaining(bbstitle, pageable);

        // 4. 실제 검색된 게시글 수 출력
        System.out.println("검색된 게시글 수: " + page.getTotalElements());

        // 5. 첫 게시글의 제목 출력
        if (!page.isEmpty()) {
            System.out.println("첫 번째 게시글 제목: " + page.getContent().get(0).getBbstitle());
        }
    } */
    
    /*@Test
    @DisplayName("게시글 내용에 키워드 포함된 게시글 페이징 조회 테스트")
    public void testFindByContentKeywordWithPaging() {
        // 1. 검색할 키워드 지정 (예: "여름")
        String keyword = "야";

        // 2. 페이지 요청 설정 (0페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);

        // 3. 키워드를 포함한 게시글 내용 검색
        Page<BbsEntity> page = bbsRepository.findByBbscontentContaining(keyword, pageable);

        // 4. 검색된 전체 게시글 수 출력
        System.out.println("검색된 게시글 수: " + page.getTotalElements());

        // 5. 결과가 비어 있지 않은 경우, 첫 게시글 내용 출력
        if (!page.isEmpty()) {
            System.out.println("첫 번째 게시글 내용: " + page.getContent().get(0).getBbscontent());
        }
    } */
    
    /*@Test
    @DisplayName("제목 또는 내용에 키워드 포함된 게시글 페이징 조회 테스트")
    public void testFindByTitleOrContentContainingWithPaging() {
        // 1. 검색 키워드 지정 (예: "여름")
        String keyword = "어";

        // 2. 페이징 설정 (0페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);

        // 3. 제목 또는 내용에 키워드가 포함된 게시글 조회
        Page<BbsEntity> page = bbsRepository.findByBbstitleContainingOrBbscontentContaining(keyword, keyword, pageable);

        // 4. 전체 검색 결과 개수 출력
        System.out.println("검색된 게시글 수: " + page.getTotalElements());

        // 5. 결과가 비어 있지 않으면 첫 게시글 정보 출력
        if (!page.isEmpty()) {
            BbsEntity firstResult = page.getContent().get(0);
            System.out.println("첫 번째 게시글 제목: " + firstResult.getBbstitle());
            System.out.println("첫 번째 게시글 내용: " + firstResult.getBbscontent());
        }
    }*/
    
   /* @Test
    @DisplayName("게시판 타입과 제목 키워드로 게시글 페이징 조회 테스트")
    public void testFindByTypeAndTitleContaining() {
        // 1. 검색 조건 지정
        BoardType type = BoardType.FAQ; // 게시판 타입 예시
        String keyword = "있어";            // 제목 키워드 예시

        // 2. 페이징 설정 (0페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);

        // 3. 조건에 맞는 게시글 조회
        Page<BbsEntity> page = bbsRepository.findByBulletinTypeAndBbstitleContaining(type, keyword, pageable);

        // 4. 결과 출력
        System.out.println("검색된 게시글 수: " + page.getTotalElements());

        if (!page.isEmpty()) {
            BbsEntity firstResult = page.getContent().get(0);
            System.out.println("첫 번째 게시글 제목: " + firstResult.getBbstitle());
            System.out.println("첫 번째 게시글 내용: " + firstResult.getBbscontent());
        }
    }*/
    
    @Test
    @DisplayName("게시판 타입과 내용 키워드로 게시글 페이징 조회 테스트")
    public void testFindByTypeAndContentContaining() {
        // 1. 검색 조건
        BoardType type = BoardType.FAQ;    // 예: 공지사항 게시판
        String keyword = "+";           // 게시글 내용 중 포함될 키워드

        // 2. 페이징 정보
        Pageable pageable = PageRequest.of(0, 10); // 첫 페이지, 10개씩

        // 3. 게시글 검색
        Page<BbsEntity> page = bbsRepository.findByBulletinTypeAndBbscontentContaining(type, keyword, pageable);

        // 4. 결과 출력
        System.out.println("총 검색된 게시글 수: " + page.getTotalElements());

        if (!page.isEmpty()) {
            BbsEntity first = page.getContent().get(0);
            System.out.println("첫 번째 게시글 제목: " + first.getBbstitle());
            System.out.println("첫 번째 게시글 내용: " + first.getBbscontent());
        }
    }
}

