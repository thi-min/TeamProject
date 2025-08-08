package com.project.board;

import com.project.board.dto.BbsDto;
import com.project.board.entity.BbsEntity;
import com.project.board.exception.BbsException;
import com.project.board.repository.BbsRepository;
import com.project.board.service.BbsService;
import com.project.member.entity.MemberEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BbsServicetests {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private BbsRepository bbsRepository;

  /*  @Test
    @DisplayName("28번 게시글을 1번 회원이 수정")
    void updateBbs_fixedId_success() {
        // given
        Long fixedBulletinNum = 1L;
        Long fixedMemberNum = 1L;

        BbsDto updateDto = BbsDto.builder()
                .bbsTitle("수정된 제목입니다")
                .bbsContent("수정된 내용입니다")
                .revisionDate(LocalDateTime.now())
                .build();

        // when
        BbsDto updated = bbsService.updateBbs(fixedBulletinNum, updateDto, fixedMemberNum);

        // then
        BbsEntity bbsFromDb = bbsRepository.findById(fixedBulletinNum)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        assertEquals("수정된 제목입니다", bbsFromDb.getBbstitle());
        assertEquals("수정된 내용입니다", bbsFromDb.getBbscontent());
        assertEquals("수정된 제목입니다", updated.getBbsTitle());
    } */
    
   /* @Test
    @DisplayName("작성자가 게시글 삭제 (bulletinNum = 1, memberNum = 1)")
    void deleteBbs_byAuthor_success() {
        // given
        Long bulletinNum = 9L;
        Long memberNum = 3L; // 작성자 번호
        Long adminId = null;

        Optional<BbsEntity> optionalBbs = bbsRepository.findById(bulletinNum);
        assertTrue(optionalBbs.isPresent(), "게시글이 DB에 존재해야 합니다");

        // when
        bbsService.deleteBbs(bulletinNum, memberNum, adminId);

        // then
        boolean exists = bbsRepository.findById(bulletinNum).isPresent();
        assertFalse(exists, "게시글이 삭제되어야 합니다");
    } 
    */
    
   /* @Test
    @DisplayName("관리자 또는 작성자는 복수 게시글 삭제 가능 - 권한 없으면 예외 발생")
    void deleteBbs_multipleIds_permissionCheck() {
        // given
        List<Long> bulletinIds = List.of(24L, 25L, 26L);

        // 시나리오 1: 관리자 권한으로 삭제 성공
        Long adminId = 10L;
        Long memberIdIrrelevant = null;

        assertDoesNotThrow(() -> {
            bbsService.deleteBbs(bulletinIds, adminId, memberIdIrrelevant);
        });
    }  

    

} */
    
  /*  @Test
    @DisplayName("작성자는 본인 게시글 복수 삭제 가능")
    void deleteBbs_multipleIds_byAuthor_shouldSucceed() {
        // given
        List<Long> bulletinIds = List.of(26L, 27L); // 둘 다 memberNum = 3L이 작성했다고 가정
        Long adminId = null;
        Long memberId = 3L;

        // when & then
        assertDoesNotThrow(() -> {
            bbsService.deleteBbs(bulletinIds, adminId, memberId);
        });
    } */
    
    
  /*  @DisplayName("게시판 타입으로 전체 게시글 목록 조회")
    @Transactional
    @Test
    void testGetAllByType() {
        // Given
        BoardType type = BoardType.FAQ;

        // When
        List<BbsDto> result = bbsService.getAllByType(type);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지
        assertFalse(result.isEmpty()); // 최소 하나 이상 있는지

        // 결과의 모든 게시글이 QNA 타입인지 확인
        for (BbsDto dto : result) {
            assertEquals(BoardType.FAQ, dto.getBulletinType());
        }
    } */
    
    /*
    @DisplayName("게시판 타입으로 전체 게시글 목록 조회")
    @Transactional
    @Test
    void testGetAllByType() {
        // Given
        BoardType type = BoardType.FAQ;

        // When
        List<BbsDto> result = bbsService.getAllByType(type);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지
        assertFalse(result.isEmpty()); // 최소 하나 이상 있는지

        // 결과의 모든 게시글이 FAQ 타입인지 확인
        for (BbsDto dto : result) {
            assertEquals(BoardType.FAQ, dto.getBulletinType());
            System.out.println("게시글 제목: " + dto.getBbsTitle() + ", 작성자: " + dto.getMemberName());
        }
    } */
    
 /*   @DisplayName("게시판 타입과 정렬 조건에 따라 페이징된 게시글 조회")
    @Transactional
    @Test
    void testGetPagedPosts() {
        // Given
        BoardType type = BoardType.FAQ;
        int page = 0; // 첫 번째 페이지
        int size = 10; // 한 페이지에 5개
        Pageable pageable = PageRequest.of(page, size);
        String sort = "views"; // 또는 "registdate"

        // When
        Page<BbsDto> result = bbsService.getPagedPosts(type, sort, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().size() <= size); // 요청한 size보다 작거나 같음

        // 결과의 모든 게시글이 지정한 타입인지 확인
        for (BbsDto dto : result.getContent()) {
            assertEquals(type, dto.getBulletinType());
        }

        // 정렬 확인 (views 기준일 경우 내림차순인지 확인)
        if ("views".equals(sort)) {
            List<BbsDto> list = result.getContent();
            for (int i = 0; i < list.size() - 1; i++) {
                assertTrue(list.get(i).getViewers() >= list.get(i + 1).getViewers());
            }
        }
    } */
    
  /*  @DisplayName("게시판 타입과 정렬 조건에 따라 페이징된 게시글 조회")
    @Transactional
    @Test
    void testGetPagedPosts() {
        // Given
        BoardType type = BoardType.FAQ;
        int page = 0; // 첫 번째 페이지
        int size = 20; // 한 페이지에 10개
        Pageable pageable = PageRequest.of(page, size);
        String sort = "views"; // 또는 "registdate"

        // When
        Page<BbsDto> result = bbsService.getPagedPosts(type, sort, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().size() <= size); // 요청한 size보다 작거나 같음

        // 결과 출력 (콘솔 확인용)
        System.out.println("==== 조회된 게시글 목록 ====");
        for (BbsDto dto : result.getContent()) {
            System.out.println("제목: " + dto.getBbsTitle() +
                    ", 조회수: " + dto.getViewers() +
                    ", 등록일: " + dto.getRegistDate() +
                    ", 게시판 타입: " + dto.getBulletinType());
        }

        // 결과의 모든 게시글이 지정한 타입인지 확인
        for (BbsDto dto : result.getContent()) {
            assertEquals(type, dto.getBulletinType());
        }

        // 정렬 확인 (views 기준일 경우 내림차순인지 확인)
        if ("views".equals(sort)) {
            List<BbsDto> list = result.getContent();
            for (int i = 10; i < list.size() - 1; i++) {
                assertTrue(list.get(i).getViewers() >= list.get(i + 1).getViewers());
            }
        }
    }
*/
   
 /*   @DisplayName("게시글 검색 - 제목, 내용, 제목+내용 / 타입 필터 포함 및 미포함 (DB 실 데이터 활용)")
    @Test
    @Transactional
    void testSearchPostsWithExistingData() {
        Pageable pageable = PageRequest.of(0, 10);
        BoardType type = BoardType.FAQ; */

     /*   // 제목으로 검색 + 타입 필터
        Page<BbsDto> titleResult = bbsService.searchPosts("title", "고기", null, BoardType.FAQ, pageable);
        assertNotNull(titleResult);
        titleResult.forEach(dto -> {
            assertTrue(dto.getBbsTitle().contains("고기"));
            assertEquals(type, dto.getBulletinType());
        }); */

     /*  // 내용으로 검색 + 타입 필터
        Page<BbsDto> contentResult = bbsService.searchPosts("content", null, "내용키워드", type, pageable);
        assertNotNull(contentResult);
        contentResult.forEach(dto -> {
            assertTrue(dto.getBbsContent().contains("내용키워드"));
            assertEquals(type, dto.getBulletinType());
        }); */

     /*   // 제목+내용으로 검색 + 타입 필터
        Page<BbsDto> combinedResult = bbsService.searchPosts("title+content", "키워드", "키워드", type, pageable);
        assertNotNull(combinedResult);
        combinedResult.forEach(dto -> {
            assertEquals(type, dto.getBulletinType());
            assertTrue(dto.getBbsTitle().contains("키워드") || dto.getBbsContent().contains("키워드"));
        }); */

     /*   // 제목으로 검색 (타입 필터 없음)
        Page<BbsDto> noTypeResult = bbsService.searchPosts("title", "검색어", null, null, pageable);
        assertNotNull(noTypeResult);
        noTypeResult.forEach(dto -> {
            assertTrue(dto.getBbsTitle().contains("검색어"));
        });  */
        
   /*     Page<BbsDto> result = bbsService.searchPosts("title", "글", null, BoardType.FAQ, pageable);

        System.out.println("검색된 총 게시글 수: " + result.getTotalElements());
        System.out.println("현재 페이지에 포함된 게시글 수: " + result.getNumberOfElements());

        for (BbsDto dto : result.getContent()) {
            System.out.println("제목: " + dto.getBbsTitle());
            System.out.println("내용: " + dto.getBbsContent());
        }
         */
        
      /*  Page<BbsDto> result = bbsService.searchPosts("content", null, "뭐야", BoardType.FAQ, pageable);

        System.out.println("검색된 총 게시글 수: " + result.getTotalElements());
        System.out.println("현재 페이지에 포함된 게시글 수: " + result.getNumberOfElements());

        for (BbsDto dto : result.getContent()) {
            System.out.println("제목: " + dto.getBbsTitle());
            System.out.println("내용: " + dto.getBbsContent());
            System.out.println("----------------------------");
        } */

       /* String keyword = "기";
        Page<BbsDto> result = bbsService.searchPosts("title+content", keyword , keyword , BoardType.FAQ, pageable);

        System.out.println("검색된 총 게시글 수: " + result.getTotalElements());
        System.out.println("현재 페이지에 포함된 게시글 수: " + result.getNumberOfElements());

        for (BbsDto dto : result.getContent()) {
            System.out.println("제목: " + dto.getBbsTitle());
            System.out.println("내용: " + dto.getBbsContent());
            System.out.println("----------------------------");
        }
        
    } */
    
    
   


}
