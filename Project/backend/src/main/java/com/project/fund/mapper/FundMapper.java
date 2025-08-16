package com.project.fund.mapper;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.entity.FundEntity;
import com.project.member.entity.MemberEntity;

import org.springframework.stereotype.Component;

/**
 * 간단한 수동 매퍼. MapStruct 사용을 원하면 대체 가능합니다.
 */
@Component
public class FundMapper {

    public FundEntity toEntity(FundRequestDto dto) {
        if (dto == null) return null;

        FundEntity e = new FundEntity();
        // fundId은 생성시 null
        // member은 서비스에서 설정 (entityManager.getReference)
        e.setFundSponsor(dto.getFundSponsor());
        e.setFundPhone(dto.getFundPhone());
        e.setFundBirth(dto.getFundBirth());
        e.setFundType(dto.getFundType());
        e.setFundMoney(dto.getFundMoney());
        e.setFundTime(dto.getFundTime());
        e.setFundItem(dto.getFundItem());
        e.setFundNote(dto.getFundNote());
        e.setFundBank(dto.getFundBank());
        e.setFundAccountNum(dto.getFundAccountNum());
        e.setFundDepositor(dto.getFundDepositor());
        e.setFundDrawlDate(dto.getFundDrawlDate());
        e.setFundCheck(dto.getFundCheck());
        return e;
    }

    public FundResponseDto toDto(FundEntity e) {
        if (e == null) return null;
        Long memberId = null;
        MemberEntity m = e.getMember();
        if (m != null) {
            try {
                // MemberEntity 필드명이 memberNum 라는 가정
                // getter 가 getMemberNum 이면 호출
                memberId = (Long) m.getClass().getMethod("getMemberNum").invoke(m);
            } catch (Exception ex) {
                // 안전하게 null로 둠
            }
        }

        return FundResponseDto.builder()
                .fundId(e.getFundId())
                .memberId(memberId)
                .fundSponsor(e.getFundSponsor())
                .fundPhone(e.getFundPhone())
                .fundBirth(e.getFundBirth())
                .fundType(e.getFundType())
                .fundMoney(e.getFundMoney())
                .fundTime(e.getFundTime())
                .fundItem(e.getFundItem())
                .fundNote(e.getFundNote())
                .fundBank(e.getFundBank())
                .fundAccountNum(e.getFundAccountNum())
                .fundDepositor(e.getFundDepositor())
                .fundDrawlDate(e.getFundDrawlDate())
                .fundCheck(e.getFundCheck())
                .build();
    }

    public void updateEntityFromDto(FundRequestDto dto, FundEntity e) {
        if (dto == null || e == null) return;
        e.setFundSponsor(dto.getFundSponsor());
        e.setFundPhone(dto.getFundPhone());
        e.setFundBirth(dto.getFundBirth());
        e.setFundType(dto.getFundType());
        e.setFundMoney(dto.getFundMoney());
        // fundTime는 보통 생성 시간/서버 시간으로 관리. 필요 시 덮어쓰기 허용.
        if (dto.getFundTime() != null) {
            e.setFundTime(dto.getFundTime());
        }
        e.setFundItem(dto.getFundItem());
        e.setFundNote(dto.getFundNote());
        e.setFundBank(dto.getFundBank());
        e.setFundAccountNum(dto.getFundAccountNum());
        e.setFundDepositor(dto.getFundDepositor());
        e.setFundDrawlDate(dto.getFundDrawlDate());
        e.setFundCheck(dto.getFundCheck());
    }
}