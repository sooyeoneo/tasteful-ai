package com.example.tastefulai.domain.image.controller;

import com.example.tastefulai.domain.image.dto.ProfileRequestDto;
import com.example.tastefulai.domain.image.dto.ProfileResponseDto;
import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.domain.member.service.MemberService;
import com.example.tastefulai.global.common.dto.CommonResponseDto;
import com.example.tastefulai.global.config.auth.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/profiles")
public class MemberProfileController {

    private final MemberService memberService;

    // 닉네임 수정 (이후 MemberController 로 이동 예정)
    @PatchMapping
    public ResponseEntity<CommonResponseDto<Void>> updateNickname(@AuthenticationPrincipal MemberDetailsImpl memberDetailsImpl,
                                                                  @RequestBody ProfileRequestDto profileRequestDto) {

        Long memberId = memberDetailsImpl.getId();

        memberService.updateNickname(memberId, profileRequestDto.getNickname());

        return new ResponseEntity<>(new CommonResponseDto<>("닉네임 변경 완료", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<ProfileResponseDto>> getProfile(@AuthenticationPrincipal MemberDetailsImpl  memberDetailsImpl) {

        Long memberId = memberDetailsImpl.getId();

        ProfileResponseDto profileResponseDto = memberService.getMemberProfile(memberId);

        return new ResponseEntity<>(new CommonResponseDto<>("프로필 조회 완료", profileResponseDto), HttpStatus.OK);
    }
}
