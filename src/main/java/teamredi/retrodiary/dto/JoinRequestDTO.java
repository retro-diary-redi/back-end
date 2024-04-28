package teamredi.retrodiary.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDTO {

    // 이메일
    private String username;

    private String password;

    private String nickname;

    private String email;

    @Builder
    public JoinRequestDTO(String username, String password, String nickname, String email) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
}
