package com.project.animal.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.animal.entity.AnimalEntity;
import com.project.animal.repository.AnimalFileRepository;
import com.project.animal.repository.AnimalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalFileRepository animalFileRepository;

    @Transactional(readOnly = true)
    public List<AnimalEntity> listAll() {
        return animalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AnimalEntity get(Long id) {
        return animalRepository.findById(id).orElse(null);
    }

    @Transactional
    public AnimalEntity create(AnimalEntity entity) {
        return animalRepository.save(entity);
    }

    @Transactional
    public AnimalEntity update(AnimalEntity entity) {
        return animalRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        animalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<Long> findFileIds(Long animalId) {
        return animalFileRepository.findByAnimalAnimalId(animalId)
                .stream().map(f -> f.getAnimalFileId()).collect(Collectors.toSet());
    }
}