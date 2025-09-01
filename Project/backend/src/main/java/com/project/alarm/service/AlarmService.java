package com.project.alarm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.alarm.dto.AlarmResponseDto;
import com.project.alarm.repository.AlarmRepository;
import com.project.reserve.entity.ReserveState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional(readOnly = true)
    public List<AlarmResponseDto> getRecentAlarms(Long memberNum) {
        return alarmRepository.findTop5ByMember_MemberNumOrderByUpdateTimeDesc(memberNum)
                .stream()
                .filter(r -> r.getReserveState() == ReserveState.DONE
                          || r.getReserveState() == ReserveState.REJ) // DONE, REJ만
                .map(r -> {
                    String message = r.getReserveState() == ReserveState.DONE
                            ? "예약이 변경되었습니다."
                            : "예약이 변경되었습니다.";
                    return new AlarmResponseDto(message, r.getUpdateTime());
                })
                .collect(Collectors.toList());
    }
}