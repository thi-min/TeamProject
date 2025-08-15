package com.project.admin.service;

import com.project.admin.dto.*;
import com.project.admin.entity.AdminInviteToken;

public interface AdminInviteService {
    AdminInviteCreateResponseDto createInvite(String issuedBy, AdminInviteCreateRequestDto req);
    AdminInviteToken getValidInviteOrThrow(String token);
    AdminInviteVerifyResponseDto verifyInvite(String token);
    AdminSignupByInviteResponseDto acceptInvite(AdminSignupByInviteRequestDto req);
}
