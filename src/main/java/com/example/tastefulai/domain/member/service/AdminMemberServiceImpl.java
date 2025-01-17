package com.example.tastefulai.domain.member.service;

import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.domain.member.enums.MemberRole;
import com.example.tastefulai.domain.member.repository.AdminMemberRepository;
import com.example.tastefulai.global.error.errorcode.ErrorCode;
import com.example.tastefulai.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberRepository adminMemberRepository;

    // 회원 탈퇴(ADMIN 전용)
    @Override
    @Transactional
    public void deleteMemberByAdmin(Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // 현재 사용자가 ADMIN 권한을 가지고 있지 않으면 예외 처리
        if (!isAdmin) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_OPERATION);
        }

        // 삭제 대상 사용자 확인
        Member targetMember = adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELETE_MEMBER_NOT_FOUND));

        // 삭제 대상이 ADMIN 권한을 가진 경우 삭제 불가
        if (targetMember.getMemberRole() == MemberRole.ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_CANNOT_REMOVE_ADMIN);
        }

        // 소프트 삭제 처리
        targetMember.softDelete();
        adminMemberRepository.save(targetMember);
    }

    // 권한 변경(ADMIN 전용)
    @Override
    @Transactional
    public void updateMemberRole(Long memberId, String memberRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // 현재 사용자가 ADMIN 권한을 가지고 있지 않으면 예외 처리
        if (!isAdmin) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_OPERATION);
        }

        // 변경 대상 회원 확인
        Member targetMember = adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //  탈퇴 회원 여부 확인
        if (targetMember.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.CANNOT_MODIFY_DEACTIVATED_MEMBER);
        }

        // 동일 권한 사용자간 변경 방지
        if (targetMember.getMemberRole() == MemberRole.ADMIN) {
            throw new CustomException(ErrorCode.CANNOT_CHANGE_TO_SAME_ROLE);
        }

        targetMember.updateMemberRole(MemberRole.valueOf(memberRole));

        adminMemberRepository.save(targetMember);
    }
}
