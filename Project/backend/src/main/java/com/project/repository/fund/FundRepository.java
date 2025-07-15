package com.project.repository.fund;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.fund.FundEntity;

public interface FundRepository extends JpaRepository<FundEntity, Integer> {

}
