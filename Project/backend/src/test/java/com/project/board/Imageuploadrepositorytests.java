package com.project.board;

import com.project.board.dto.BbsDto;
import com.project.board.dto.ImageBbsDto;
import com.project.board.repository.BbsRepository;
import com.project.board.repository.ImageBbsRepository;
import com.project.board.service.BbsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class Imageuploadrepositorytests {

    @Autowired
    private BbsService bbsService;

    @Autowired
    private BbsRepository bbsRepository;

    @Autowired
    private ImageBbsRepository imageBbsRepository;

    @Test
    @DisplayName("게시글과 이미지 첨부를 함께 저장")
    void 게시글과이미지첨부_동시저장_테스트() throws Exception {

        // 1. 게시글 DTO 생성
        BbsDto dto = BbsDto.builder()
                .bbsTitle("테스트 이미지 게시글")
                .bbsContent("이것은 테스트용 이미지 게시글입니다.")
                .boardType(BoardType.POTO)  // enum 수정
                .build();

        // 2. 모의 이미지 파일 2개 생성
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        List<MockMultipartFile> mockFiles = List.of(file1, file2);

        // 3. 작성자 정보 (회원 번호, 관리자 ID)
        Long memberNum = 1L;   // 회원 작성자 ID 예시
        Long adminId = null;   // 관리자 없음

        // 4. 게시글 및 이미지 저장
        BbsDto savedBbs = bbsService.createBbs(dto, memberNum, adminId, mockFiles);

        // 5. 저장 결과 검증
        assertNotNull(savedBbs, "저장된 게시글이 null이면 안됩니다.");
        assertNotNull(savedBbs.getBbsId(), "저장된 게시글 ID가 null이면 안됩니다.");

        // 6. 이미지 첨부 개수 검증
        List<ImageBbsDto> imageList = imageBbsRepository.findByBbs_BulletinNum(savedBbs.getBbsId())
                .stream()
                .map(ImageBbsDto::fromEntity)
                .toList();

        assertEquals(mockFiles.size(), imageList.size(), "첨부된 이미지 개수가 일치해야 합니다.");

        System.out.println("✅ 게시글 ID: " + savedBbs.getBbsId());
        System.out.println("✅ 저장된 이미지 개수: " + imageList.size());
    }
}

