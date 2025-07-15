package com.project.service.fund;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.entity.fund.FundEntity;
import com.project.repository.fund.FundRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FundService {

	private final FundRepository fundRepository;
	
	public void saveFund(FundEntity entity) {
		fundRepository.save(entity);
	}
	
	public List<FundEntity> getAllFund(){
		return fundRepository.findAll();
	}
	
}
