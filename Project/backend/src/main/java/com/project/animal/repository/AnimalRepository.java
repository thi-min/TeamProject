package com.project.animal.repository;

import com.project.animal.entity.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, Long> { }