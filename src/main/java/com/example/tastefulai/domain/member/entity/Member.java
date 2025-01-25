package com.example.tastefulai.domain.member.entity;

import com.example.tastefulai.domain.image.dto.ProfileResponseDto;
import com.example.tastefulai.domain.image.entity.Image;
import com.example.tastefulai.domain.member.enums.GenderRole;
import com.example.tastefulai.domain.member.enums.MemberRole;
import com.example.tastefulai.domain.taste.entity.Taste;
import com.example.tastefulai.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderRole genderRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Image> images;

    // 연관 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Taste> tastes = new ArrayList<>();

    // 생성자
    public Member(MemberRole memberRole, String email, String password, String nickname, Integer age,
                  GenderRole genderRole, LocalDateTime deletedAt) {
        this.memberRole = memberRole;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.genderRole = genderRole;
        this.deletedAt = deletedAt;
    }

    public List<GrantedAuthority> getAuthorities() {
        return List.of(memberRole)
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public static ProfileResponseDto toDto (Member member) {

        return new ProfileResponseDto(
                member.getNickname(),
                (member.getImages().isEmpty()) ? null : member.getImages().getFirst().getImageUrl(),
                member.getCreatedAt().toLocalDate(),
                "");
    }
}
