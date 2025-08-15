package com.project.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.admin.entity.AdminInviteToken;

//관리자 회원가입 토큰 발급
public interface AdminInviteTokenRepository extends JpaRepository<AdminInviteToken, Long> {
    Optional<AdminInviteToken> findByToken(String token);
    boolean existsByToken(String token);
}
