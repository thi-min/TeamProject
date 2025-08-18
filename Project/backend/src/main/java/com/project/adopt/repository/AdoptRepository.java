package com.project.adopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.entity.AdoptState;

@Repository
public interface AdoptRepository extends JpaRepository<AdoptEntity, Long> {
    List<AdoptEntity> findByMemberMemberNum(Long memberNum);
    List<AdoptEntity> findByAdoptState(AdoptState state);
}