package com.project.adopt.repository;

import com.example.adopt.domain.Adopt;
import com.example.adopt.domain.AdoptState;
import com.example.adopt.dto.AdoptListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdoptRepository extends JpaRepository<Adopt, Long> {

    // 페이징된 요약 목록 (관리자용)
    @Query("select new com.example.adopt.dto.AdoptListDto(a.adoptNum, m.memberNum, m.name, " +
           "an.animalId, a.adoptTitle, a.consultDt, a.visitDt, a.adoptState) " +
           "from Adopt a join a.member m left join a.animal an")
    Page<AdoptListDto> findAdoptSummaries(Pageable pageable);

    // 상태별 조회
    List<Adopt> findByAdoptState(AdoptState state);

    // member 기준 조회
    List<Adopt> findByMember_MemberNum(Long memberNum);
}