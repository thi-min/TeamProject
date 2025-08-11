package com.project.adopt.service;

import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.repository.AdoptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptService {
    private final AdoptRepository adoptRepository;

    @Transactional(readOnly = true)
    public List<AdoptEntity> listAll() {
        return adoptRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AdoptEntity get(Long id) {
        return adoptRepository.findById(id).orElse(null);
    }

    @Transactional
    public AdoptEntity create(AdoptEntity e) {
        return adoptRepository.save(e);
    }

    @Transactional
    public AdoptEntity update(AdoptEntity e) {
        return adoptRepository.save(e);
    }

    @Transactional
    public void delete(Long id) {
        adoptRepository.deleteById(id);
    }
}