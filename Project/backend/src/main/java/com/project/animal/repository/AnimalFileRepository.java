package com.project.animal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.animal.entity.AnimalFileEntity;

@Repository
public interface AnimalFileRepository extends JpaRepository<AnimalFileEntity, Long> {
    List<AnimalFileEntity> findByAnimalAnimalId(Long animalId);
}