package com.project.alarm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.alarm.dto.AlarmResponseDto;
import com.project.alarm.service.AlarmService;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    private MemberMeResponseDto getMemberFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;

        String token = header.substring(7);
        String memberId = jwtTokenProvider.getMemberIdFromToken(token);
        if (memberId == null) return null;

        return memberService.getMyInfo(memberId);
    }

    @GetMapping("/api/alarm/list")
    public ResponseEntity<List<AlarmResponseDto>> getAlarms(HttpServletRequest request) {
        MemberMeResponseDto member = getMemberFromRequest(request);
        if (member == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<AlarmResponseDto> alarms = alarmService.getRecentAlarms(member.getMemberNum());
        return ResponseEntity.ok(alarms);
    }
}