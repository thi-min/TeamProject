package com.project.common.service;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.entity.ClosedDay;
import com.project.common.repository.ClosedDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClosedDayServiceImpl implements ClosedDayService {
    private final ClosedDayRepository closedDayRepository;

    @Override
    public void setClosedDay(ClosedDayRequestDto dto) {
        ClosedDay closedDay = ClosedDay.builder()
                .closedDate(dto.getClosedDate())
                .isClosed(dto.getIsClosed())
                .build();

        closedDayRepository.save(closedDay);
    }

    @Override
    public void deleteClosedDay(LocalDate date) {
        if (closedDayRepository.existsByClosedDate(date)) {
            closedDayRepository.deleteByClosedDate(date);
        } else {
            throw new IllegalArgumentException("해당 날짜는 휴무일로 등록되어 있지 않습니다.");
        }
    }

    @Override
    public boolean isClosed(LocalDate date) {
        return closedDayRepository.findByClosedDate(date)
                .map(ClosedDay::getIsClosed)
                .orElse(false);
    }
}
