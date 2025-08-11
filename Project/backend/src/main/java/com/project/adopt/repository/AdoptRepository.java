package com.project.adopt.repository;

import com.project.adopt.entity.AdoptEntity;
import com.project.common.enums.AdoptState;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptRepository extends JpaRepository<AdoptEntity, Long> {
    List<AdoptEntity> findByMemberMemberNum(Long memberNum);
    List<AdoptEntity> findByAdoptState(AdoptState state);
}