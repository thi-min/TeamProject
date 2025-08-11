package com.project.animal.mapper;

import com.project.animal.dto.AnimalRequestDto;
import com.project.animal.dto.AnimalResponseDto;
import com.project.animal.entity.AnimalEntity;
import com.project.animal.entity.AnimalFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AnimalMapper {
    AnimalEntity toEntity(AnimalRequestDto dto);

    @Mapping(target = "fileIds", source = "files", qualifiedByName = "filesToIds")
    AnimalResponseDto toDto(AnimalEntity entity);

    @Named("filesToIds")
    default Set<Long> filesToIds(Set<AnimalFileEntity> files) {
        if (files == null) return null;
        return files.stream().map(AnimalFileEntity::getAnimalFileId).collect(Collectors.toSet());
    }
}