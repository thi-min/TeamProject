package com.project.animal.repository;

import com.example.adopt.domain.Animal;
import com.example.adopt.dto.AnimalListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    // 페이징 요약 (검색어로 이름/품종 필터 가능)
    @Query("select new com.example.adopt.dto.AnimalListDto(a.animalId, a.animalName, a.animalBreed, a.animalSex, a.animalState, a.animalDate) " +
           "from Animal a " +
           "where (:q is null or lower(a.animalName) like lower(concat('%', :q, '%')) or lower(a.animalBreed) like lower(concat('%', :q, '%')))")
    Page<AnimalListDto> findAnimalSummariesByQuery(String q, Pageable pageable);

    Optional<Animal> findByAnimalId(Long animalId);
}