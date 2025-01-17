package com.example.tastefulai.global.config;

import com.example.tastefulai.global.config.filter.JwtAuthFilter;
import com.example.tastefulai.global.constant.EndpointConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  //@Configuration 어노테이션 포함 및 보안 기능 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // 비밀번호 암호화(BCryptPasswordEncoder) 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 애플리케이션 보안 설정(Spring Security 6.x 이상 대응)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //브라우저 팝업창 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // CORS 설정 활성화
                .cors(cors -> cors.configure(httpSecurity))

                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 요청 권한 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/map", "location", EndpointConstants.AUTH_SIGNUP, EndpointConstants.AUTH_LOGIN).permitAll() // 회원가입과 로그인 요청 허용
                        .requestMatchers("/api/location/**").authenticated()
                        .requestMatchers("/ws-chat/**").permitAll()
                        .requestMatchers("/test").permitAll()
                        .anyRequest().authenticated() // 그 외 요청 인증 필요
                )

                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 세션 정책 설정 (무상태)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 인증 실패 시 401 반환
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Authentication failed\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // 권한 부족 시 403 반환
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"권한Access denied\"}");
                        })
                )

                // 최종 보안 필터 체인 빌드
                .build();
    }
}
