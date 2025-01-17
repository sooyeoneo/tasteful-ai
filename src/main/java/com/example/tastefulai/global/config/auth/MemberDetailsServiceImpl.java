package com.example.tastefulai.global.config.auth;

import com.example.tastefulai.domain.member.dto.LoginRequestDto;
import com.example.tastefulai.domain.member.entity.Member;
import com.example.tastefulai.domain.member.repository.MemberRepository;
import com.example.tastefulai.global.error.errorcode.ErrorCode;
import com.example.tastefulai.global.error.exception.CustomException;
import com.example.tastefulai.global.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 로그인 요청 처리
    public String login(LoginRequestDto loginRequest) {
        // 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 사용자 인증 완료시, JWT 토큰 생성
        return jwtProvider.generateToken(member.getEmail(), jwtProvider.getAccessTokenExpiryMillis());
    }

    // 사용자 정보 로드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberDetailsImpl(member);
    }
}