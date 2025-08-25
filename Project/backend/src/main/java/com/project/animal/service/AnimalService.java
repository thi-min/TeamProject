package com.project.animal.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.animal.entity.AnimalEntity;
import com.project.animal.repository.AnimalFileRepository;
import com.project.animal.repository.AnimalRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalFileRepository animalFileRepository;
    
    //동물 데이터 조회
    @Transactional(readOnly = true)
    public Page<AnimalEntity> listAll(Pageable pageable) {
        return animalRepository.findAll(pageable);
    }
    
    //특정 동물 데이터 조회 , 없을시 null 반환
    @Transactional(readOnly = true)
    public AnimalEntity get(Long id) {
        return animalRepository.findById(id).orElse(null);
    }
    //동물 데이터 저장
    @Transactional
    public AnimalEntity create(AnimalEntity entity) {
        return animalRepository.save(entity);
    }
    // 동물 데이터 갱신
    @Transactional
    public AnimalEntity update(AnimalEntity entity) {
        return animalRepository.save(entity);
    }
    //동물 데이터 제거
    @Transactional
    public void delete(Long id) {
        animalRepository.deleteById(id);
    }
    //특정 동물에 연결된 파일 목록 조회
    @Transactional(readOnly = true)
    public Set<Long> findFileIds(Long animalId) {
        return animalFileRepository.findByAnimalAnimalId(animalId)
                .stream().map(f -> f.getAnimalFileId()).collect(Collectors.toSet());
        //조회 파일 entity목록 stream변환 후, map을 사용해서 animalfileid만 추출, 
        	//collect를 통해 추출 된 id는 set<long>형태로 수집 반환
    }
    @Transactional(readOnly = true)
    public AnimalEntity getAnimal(Long animalId) {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with id: " + animalId));
    }
}