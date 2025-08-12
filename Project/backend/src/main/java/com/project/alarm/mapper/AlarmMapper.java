package com.project.alarm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.project.alarm.dto.AlarmRequestDto;
import com.project.alarm.dto.AlarmResponseDto;
import com.project.alarm.entity.AlarmEntity;
import com.project.member.entity.MemberEntity;

@Mapper(componentModel = "spring")
public interface AlarmMapper {
    @Mapping(target = "member", source = "memberNum", qualifiedByName = "memberNumToEntity")
    AlarmEntity toEntity(AlarmRequestDto dto);

    @Mapping(target = "memberNum", source = "member.memberNum")
    AlarmResponseDto toDto(AlarmEntity entity);

    @Named("memberNumToEntity")
    default MemberEntity memberNumToEntity(Long memberNum) {
        if (memberNum == null) return null;
        MemberEntity m = new MemberEntity();
        m.setMemberNum(memberNum);
        return m;
    }
}