package com.example.tastefulai.domain.member.service;

import com.example.tastefulai.domain.member.dto.MemberResponseDto;
import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.domain.member.enums.GenderRole;
import com.example.tastefulai.domain.member.enums.MemberRole;
import com.example.tastefulai.global.common.dto.JwtAuthResponse;

public interface MemberService {

    Member findByEmail(String email);

    MemberResponseDto signup(String email, String password, String nickname, Integer age, GenderRole genderRole, MemberRole memberRole);

    JwtAuthResponse login(String email, String password);

    void logout(String token);

    void changePassword(Long memberId, String currentPassword, String newPassword);

    void verifyPassword(Long memberId, String password);

    void deleteMember(Long memberId);
}