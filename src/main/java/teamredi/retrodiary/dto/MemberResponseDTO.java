package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.enumerate.Role;


@NoArgsConstructor
@Getter
public class MemberResponseDTO {


    private String username;

    private String password;

    private String nickname;

    private String email;

    private Role role;

    @Builder
    public MemberResponseDTO(String username, String password, String nickname, String email, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    @Builder
    public Member toEntity() {
        return Member.builder()
                .username(getUsername())
                .password(getPassword())
                .nickname(getNickname())
                .email(getEmail())
                .role(getRole())
                .build();
    }
}
