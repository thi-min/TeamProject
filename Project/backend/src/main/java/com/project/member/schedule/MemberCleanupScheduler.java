package com.project.member.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

//회원상태가 OUT이고 탈퇴일로부터 7일 지난 회원들을 자동으로 삭제하는 스케줄러
@Component
@RequiredArgsConstructor
public class MemberCleanupScheduler {
	private final MemberRepository memberRepository;
	
	//매일 00시 실행
	//@Scheduled(cron = "0 * * * * * ")
	@Scheduled(cron = "0 0 0 * * * ")
	public void deleteOutMemberAfter7Days() {
		LocalDateTime threshold = LocalDateTime.now().minusDays(7); //.minusMinutes(1);
		
		List<MemberEntity> expired = memberRepository
				.findByMemberStateAndOutDateBefore(MemberState.OUT, threshold);
		
		if(!expired.isEmpty()) {
			memberRepository.deleteAll(expired);
			System.out.println("탈퇴 후 7일 경과 회원" + expired.size() + "명 삭제 완료.");
		}
		
	}
	
}
