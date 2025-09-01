package com.project.animal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.animal.entity.AnimalEntity;


@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, Long> {
	
}