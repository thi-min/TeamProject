package com.project.adopt.mapper;

import com.project.adopt.dto.AdoptRequestDto;
import com.project.adopt.dto.AdoptResponseDto;
import com.project.adopt.entity.AdoptEntity;
import com.project.animal.entity.AnimalEntity;
import com.project.member.entity.MemberEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdoptMapper {

    @Mapping(target = "member", source = "memberNum", qualifiedByName = "memberNumToEntity")
    @Mapping(target = "animal", source = "animalId", qualifiedByName = "animalIdToEntity")
    AdoptEntity toEntity(AdoptRequestDto dto);

    @Mapping(target = "memberNum", source = "member.memberNum")
    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "animalId", source = "animal.animalId")
    @Mapping(target = "animalName", source = "animal.animalName")
    AdoptResponseDto toDto(AdoptEntity entity);

    @Named("memberNumToEntity")
    default MemberEntity memberNumToEntity(Long memberNum) {
        if (memberNum == null) return null;
        MemberEntity m = new MemberEntity();
        m.setMemberNum(memberNum);
        return m;
    }

    @Named("animalIdToEntity")
    default AnimalEntity animalIdToEntity(Long animalId) {
        if (animalId == null) return null;
        AnimalEntity a = new AnimalEntity();
        a.setAnimalId(animalId);
        return a;
    }
}