package com.project.animal.repository;

import com.project.animal.entity.AnimalFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalFileRepository extends JpaRepository<AnimalFileEntity, Long> {
    List<AnimalFileEntity> findByAnimalAnimalId(Long animalId);
}