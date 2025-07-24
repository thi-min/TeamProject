package com.project.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;


@SpringBootTest
public class MemberRepositoryTests2 {
	@Test
	void memberEntity_에_admin_필드가_정상적으로_존재하는지_확인() {
	    MemberEntity member = new MemberEntity();
	    member.setAdmin(new AdminEntity());

	    assertThat(member.getAdmin()).isNotNull();
	}
}

//Optional 데이터가 있을수도 없을수도있음.