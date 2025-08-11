package com.project.mapdata.mapper;

import com.project.mapdata.dto.MapDataRequestDto;
import com.project.mapdata.dto.MapDataResponseDto;
import com.project.mapdata.entity.MapDataEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapDataMapper {
    MapDataEntity toEntity(MapDataRequestDto dto);
    MapDataResponseDto toDto(MapDataEntity entity);
}