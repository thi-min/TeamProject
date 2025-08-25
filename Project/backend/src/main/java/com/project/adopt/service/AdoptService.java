package com.project.adopt.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.repository.AdoptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdoptService {
    private final AdoptRepository adoptRepository;

    //입양 리스트 전체 확인
    @Transactional(readOnly = true)
    public Page<AdoptEntity> listAll(Pageable pageable) {
        return adoptRepository.findAll(pageable);
    }
    //특정 입양 번호 조회
    @Transactional(readOnly = true)
    public AdoptEntity get(Long id) {
        return adoptRepository.findById(id).orElse(null);
    }
    //client 입양 조회
    @Transactional(readOnly = true)
    public Page<AdoptEntity> listByMemberNum(Long memberNum, Pageable pageable){
        return adoptRepository.findByMember_MemberNum(memberNum, pageable);
    }
    
    //입양 데이터 저장
    @Transactional
    public AdoptEntity create(AdoptEntity e) {
        return adoptRepository.save(e);
    }
    //입양 데이터 갱신
    @Transactional
    public AdoptEntity update(AdoptEntity e) {
        return adoptRepository.save(e);
    }
    //특정 입양 번호로 입양 데이터 제거
    @Transactional
    public void delete(Long id) {
        adoptRepository.deleteById(id);
    }
}