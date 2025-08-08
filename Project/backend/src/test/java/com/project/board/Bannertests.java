package com.project.board;

import com.project.banner.BannerService;
import com.project.banner.dto.BannerCreateDto;
import com.project.banner.dto.BannerListDto;
import com.project.banner.dto.BannerUpdateDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.banner.BannerEntity;
import com.project.banner.BannerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback(false)
public class Bannertests {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private AdminRepository adminRepository;

  /*  @Test
    @DisplayName("배너 생성 통합 테스트")
    public void testCreateBanner() throws IOException {
        // 1. 관리자 계정 저장 (테스트용 관리자)
        AdminEntity admin = AdminEntity.builder()
                .adminId("admin001")
                .adminName("관리자")
                .adminPw("test1234")
                .adminEmail("admin@test.com")
                .adminPhone("010-1234-5678")
                .registDate(LocalDateTime.now())
                .connectData(LocalDateTime.now())
                .build();

        // 👉 저장을 누락하지 마세요!
        adminRepository.save(admin);

        // 2. Banner DTO 설정
        BannerCreateDto dto = BannerCreateDto.builder()
                .adminId("admin001")
                .title("테스트 배너")
                .subTitle("테스트 부제목")
                .altText("대체 텍스트")
                .linkUrl("https://example.com")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 8, 31))
                .visible(true)
                .build();

        // 3. 테스트용 파일 설정 (로컬 D:/temp/duke.jpg 사용)
        File file = new File("D:/temp/duke.jpg");
        FileInputStream fis = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(
                "file", file.getName(), "image/jpeg", fis);

        // 4. 배너 생성 호출
        bannerService.createBanner(dto, multipartFile);

        // 5. 검증
        List<BannerEntity> allBanners = bannerRepository.findAll();
        assertFalse(allBanners.isEmpty());
        assertEquals("테스트 배너", allBanners.get(0).getTitle());
    } */
   /* 
    @Test
    @DisplayName("기존 관리자 계정으로 배너 생성 테스트")
    public void testCreateBannerWithExistingAdmin() throws IOException {
        String existingAdminId = "admin001";  // 이미 DB에 존재한다고 가정하는 관리자 아이디

        // 1. Banner DTO 설정 - 관리자 아이디만 넣음
        BannerCreateDto dto = BannerCreateDto.builder()
                .adminId(existingAdminId)
                .title("배너5 테스트")
                .subTitle("테스트 진짜제목5")
                .altText("대체 텍스트5")
                .linkUrl("https://example5.com")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 8, 31))
                .visible(true)
                .build();

        // 2. 테스트용 파일 설정 (로컬 D:/temp/duke.jpg 사용)
        File imageFile = new File("D:/temp/cloth1.jpg");
        assertTrue(imageFile.exists(), "테스트 이미지 파일이 존재하지 않습니다: " + imageFile.getPath());

        FileInputStream fis = new FileInputStream(imageFile);
        MultipartFile multipartFile = new MockMultipartFile(
                "file", imageFile.getName(), "image/jpeg", fis);

        // 3. 배너 생성 호출
        bannerService.createBanner(dto, multipartFile);

       // 4. 저장된 배너 검증
        List<BannerEntity> allBanners = bannerRepository.findAll();
        assertFalse(allBanners.isEmpty(), "배너가 저장되지 않았습니다.");

        BannerEntity savedBanner = allBanners.get(0);
        assertEquals("기존 관리자 배너2", savedBanner.getTitle());
        assertEquals(existingAdminId, savedBanner.getAdmin().getAdminId());
        assertNotNull(savedBanner.getImageUrl(), "이미지 파일명이 저장되지 않았습니다."); 
    }  */
    
   /* @Test
    @DisplayName("기존 관리자 아이디로 AdminEntity 조회 테스트")
    public void testFindAdminByAdminId() {
        String existingAdminId = "admin001"; // 이미 DB에 존재하는 관리자 아이디

        // Optional로 받는 메서드 사용
        Optional<AdminEntity> adminOpt = adminRepository.findFirstByAdminId(existingAdminId);

        assertTrue(adminOpt.isPresent(), "관리자 정보를 찾을 수 없습니다.");

        AdminEntity foundAdmin = adminOpt.get();
        assertEquals(existingAdminId, foundAdmin.getAdminId());
        System.out.println("조회된 관리자 이름: " + foundAdmin.getAdminName());
    } */
    
 /*   @Test
    @DisplayName("배너 아이디 리스트로 배너 일괄 삭제 테스트")
    public void testDeleteByBannerIdIn() {
        // 1. 삭제할 배너 ID 리스트 (2 ~ 3번)
        List<Long> idsToDelete = List.of(2L, 3L);

        // 2. 삭제 실행
        bannerRepository.deleteByBannerIdIn(idsToDelete);

        // 3. 삭제된 배너들이 DB에 존재하지 않는지 확인
        List<BannerEntity> remainingBanners = bannerRepository.findAll();

        // 삭제 대상 ID들이 남아있지 않은지 검사
        boolean anyDeletedIdsExist = remainingBanners.stream()
            .anyMatch(b -> idsToDelete.contains(b.getBannerId()));

        assertFalse(anyDeletedIdsExist, "삭제 대상 배너가 DB에 아직 존재합니다.");

        // 남아있는 배너 개수 출력 (참고용)
        System.out.println("삭제 후 남은 배너 개수: " + remainingBanners.size());
    }  */

    
 /*   @Test
    @DisplayName("배너 아이디 리스트로 배너 일괄 삭제 테스트")
    @Transactional
    @Rollback(false)  // 테스트 후 실제 DB 반영을 위해
    public void testDeleteByBannerIdIn() {
        List<Long> idsToDelete = List.of(2L, 3L);

        // 삭제 실행
        bannerRepository.deleteByBannerIdIn(idsToDelete);

        // 삭제 후 DB에서 해당 ID가 존재하는지 확인
        boolean existsDeletedIds = bannerRepository.findAll().stream()
            .anyMatch(b -> idsToDelete.contains(b.getBannerId()));

        assertFalse(existsDeletedIds, "삭제 대상 배너가 DB에 여전히 존재합니다.");
    } */

   /* @Test
    @DisplayName("기존 배너들 모두 조회 테스트")
    public void testGetAll() {
        List<BannerListDto> bannerList = bannerService.getAll();

        assertNotNull(bannerList, "배너 리스트가 null 이면 안 됩니다.");
        assertFalse(bannerList.isEmpty(), "배너 리스트가 비어 있으면 안 됩니다.");

        // 예시로 첫 번째 배너의 제목 출력 (디버깅용)
        System.out.println("세 번째 배너 제목: " + bannerList.get(3).getTitle());
    } */

  /*  @Test
    @DisplayName("배너 상세 조회 테스트")
    public void testGetBannerDetail() {
        // 존재하는 배너 ID로 지정 (미리 DB에 있는 값으로 바꿔주세요)
        Long bannerId = 8L;

        BannerListDto banner = bannerService.getDetail(bannerId);

        assertNotNull(banner, "배너 상세 정보가 null 이면 안 됩니다.");
        assertEquals(bannerId, banner.getBannerId(), "조회된 배너의 ID가 일치하지 않습니다.");

        // 디버깅용 출력
        System.out.println("배너 제목: " + banner.getTitle());
        System.out.println("배너 이미지 경로: " + banner.getImageUrl());
    } */
    
 /*   @Test
    @DisplayName("기존 DB에 저장된 배너 수정 테스트")
    public void testUpdateBanner() throws IOException {
        Long existingBannerId = 4L; // 수정할 배너 ID를 실제 DB에 있는 ID로 바꾸세요.

        // 1. 기존 배너 조회 (DB에 반드시 존재해야 함)
        BannerEntity existingBanner = bannerRepository.findById(existingBannerId)
                .orElseThrow(() -> new RuntimeException("수정할 배너가 존재하지 않습니다."));

        // 2. 수정용 DTO 준비
        BannerUpdateDto updateDto = BannerUpdateDto.builder()
                .title("수정된 제목3")
                .subTitle("수정된 부제목3")
                .altText("수정된 대체 텍스트")
                .linkUrl("https://modified-url.com")
                .startDate(existingBanner.getStartDate()) // 필요하면 수정
                .endDate(existingBanner.getEndDate())     // 필요하면 수정
                .visible(false)
                .build();

        // 3. 수정할 이미지 파일 (없으면 null 가능)
        File file = new File("D:/temp/cloth3.jpg");
        MultipartFile multipartFile = null;
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", fis);
        }

        // 4. 서비스의 수정 메서드 호출
        bannerService.update(existingBannerId, updateDto, multipartFile);

       // 5. 수정 결과 검증
        BannerEntity updatedBanner = bannerRepository.findById(existingBannerId)
                .orElseThrow(() -> new RuntimeException("수정 후 배너가 존재하지 않습니다."));

        assertEquals("수정된 제목2", updatedBanner.getTitle());
        assertEquals("수정된 부제목2", updatedBanner.getSubTitle());
        assertEquals("수정된 대체 텍스트", updatedBanner.getAltText());
        assertEquals("https://modified-url.com", updatedBanner.getLinkUrl());
        assertFalse(updatedBanner.getVisible());

        if (multipartFile != null) {
            assertTrue(updatedBanner.getImageUrl().contains("modified-image")); // 이미지명 포함 여부 체크
        } 
    } */
 

    
  /*  @DisplayName("배너 삭제 테스트 - 기존 DB에 있는 배너 사용")
    @Test
    @Transactional
    @Rollback(false)  // 테스트 후 실제 DB 반영을 위해
    void testDeleteExistingBanner() {
        // 💡 실제 DB에 존재하는 배너 ID로 바꿔주세요!
        Long existingBannerId = 7L;

        // 삭제 전 존재 여부 확인
        Optional<BannerEntity> beforeDelete = bannerRepository.findById(existingBannerId);
        assertTrue(beforeDelete.isPresent(), "삭제 전: 배너가 존재해야 합니다.");

        // 삭제 실행
        bannerService.delete(existingBannerId);

        // 삭제 후 존재 여부 확인
        Optional<BannerEntity> afterDelete = bannerRepository.findById(existingBannerId);
        assertTrue(afterDelete.isEmpty(), "삭제 후: 배너가 삭제되어야 합니다.");
    }
*/
    
    @DisplayName("배너 복수 삭제 테스트")
    @Test
    @Transactional
    void testDeleteBulkBanners() {
 
        List<Long> bannerIdsToDelete = List.of(4L, 5L, 6L);

        // 삭제 전 존재 여부 확인
        List<BannerEntity> beforeDelete = bannerRepository.findAllById(bannerIdsToDelete);
        assertEquals(bannerIdsToDelete.size(), beforeDelete.size(), "삭제 전: 모든 배너가 존재해야 합니다.");

        // 삭제 실행
        bannerService.deleteBulk(bannerIdsToDelete);

        // 삭제 후 존재 여부 확인
        List<BannerEntity> afterDelete = bannerRepository.findAllById(bannerIdsToDelete);
        assertTrue(afterDelete.isEmpty(), "삭제 후: 배너들이 모두 삭제되어야 합니다.");
    }

}

