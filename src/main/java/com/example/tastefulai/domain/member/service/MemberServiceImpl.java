package com.example.tastefulai.domain.member.service;

import com.example.tastefulai.domain.image.dto.ProfileResponseDto;
import com.example.tastefulai.domain.member.dto.LoginRequestDto;
import com.example.tastefulai.domain.member.dto.MemberRequestDto;
import com.example.tastefulai.domain.member.dto.MemberResponseDto;
import com.example.tastefulai.domain.member.dto.PasswordUpdateRequestDto;
import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.domain.member.enums.GenderRole;
import com.example.tastefulai.domain.member.enums.MemberRole;
import com.example.tastefulai.domain.member.repository.MemberRepository;
import com.example.tastefulai.global.common.dto.JwtAuthResponse;
import com.example.tastefulai.domain.member.validation.MemberValidation;
import com.example.tastefulai.global.error.errorcode.ErrorCode;
import com.example.tastefulai.global.error.exception.CustomException;
import com.example.tastefulai.global.error.exception.NotFoundException;
import com.example.tastefulai.global.util.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberValidation memberValidation;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BlacklistService blacklistService;

    private static final String VERIFY_PASSWORD_KEY = "verify-password:";
    private static final String REFRESH_TOKEN_KEY = "refreshToken:";

    // 1. 회원가입
    @Override
    @Transactional
    public MemberResponseDto signup(MemberRole memberRole, String email, String password, String nickname,
                                    Integer age, GenderRole genderRole) {
        // 유효성 검사
        MemberRequestDto memberRequestDto = new MemberRequestDto(memberRole, email, password, nickname, age, genderRole);
        memberValidation.validateSignUp(memberRequestDto);

        // 비밀번호 암호화 및 회원 생성
        String encodedPassword = passwordEncoder.encode(password);
        Member member = new Member(memberRole, email, encodedPassword, nickname, age, genderRole, null);
        memberRepository.save(member);

        return new MemberResponseDto(member.getId(), member.getMemberRole(), member.getEmail(), member.getNickname());
    }


    // 2. 로그인
    @Override
    public JwtAuthResponse login(String email, String password) {

        // 유효성 검사
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);
        memberValidation.validateLogin(loginRequestDto);

        // 사용자 확인
        Member member = this.memberRepository.findActiveByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 확인
        validatePassword(password, member.getPassword());

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        // RefreshToken 을 Redis 에 저장
        storeRefreshToken(email, refreshToken);

        return new JwtAuthResponse(accessToken, refreshToken);
    }


    // 3. 로그아웃
    @Override
    public void logout(String token) {
        // AccessToken 블랙리스트 등록
        blacklistService.addToBlacklist(token, jwtProvider.getAccessTokenExpiryMillis());
        log.info("AccessToken 블랙리스트 등록 완료: {}", token);
    }


    // 4. 비밀번호 변경
    @Override
    @Transactional
    public void updatePassword(String email, String currentPassword, String newPassword, String currentAccessToken) {

        // 유효성 검사
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto(currentPassword, newPassword);
        memberValidation.validatePasswordUpdate(passwordUpdateRequestDto);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 현재 비밀번호 검증
        validatePassword(currentPassword, member.getPassword());

        // 비밀번호 변경
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        deleteRefreshToken(email);
    }


    // 5. 비밀번호 검증
    @Override
    public void verifyPassword(Long memberId, String password) {
        // 유효성 검사
        memberValidation.validatePassword(password);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        validatePassword(password, member.getPassword());
        savePasswordVerification(memberId);
    }


    // 6. 회원 탈퇴
    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        if (!isPasswordVerified(memberId)) {
            throw new CustomException(ErrorCode.VERIFY_PASSWORD_REQUIRED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        member.softDelete();
        memberRepository.save(member);
        clearPasswordVerification(memberId);
    }


    // 7. 닉네임 수정
    @Override
    @Transactional
    public void updateNickname(Long memberId, String nickname) {

        Member member = findById(memberId);

        member.updateNickname(nickname);

        memberRepository.save(member);
    }


    // 8. 프로필 조회
    @Override
    public ProfileResponseDto getMemberProfile(Long memberId) {

        Member member = findById(memberId);

        return Member.toDto(member);
    }

    // **** 공통 메서드 **** //

    // 검증 상태 확인
    public boolean isPasswordVerified(Long memberId) {
        String isVerified = (String) redisTemplate.opsForValue().get(VERIFY_PASSWORD_KEY + memberId);
        return "true".equals(isVerified);
    }

    // 검증 상태 제거
    public void clearPasswordVerification(Long memberId) {
        redisTemplate.delete(VERIFY_PASSWORD_KEY + memberId);
    }

    // 비밀번호 검증 공통 로직
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_PASSWORD);
        }
    }

    private void storeRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_KEY + email,
                refreshToken,
                jwtProvider.getRefreshTokenExpiryMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    private void deleteRefreshToken(String email) {
        redisTemplate.delete(REFRESH_TOKEN_KEY + email);
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
    }

    private void savePasswordVerification(Long memberId) {
        redisTemplate.opsForValue().set(VERIFY_PASSWORD_KEY + memberId, "true");
    }
}