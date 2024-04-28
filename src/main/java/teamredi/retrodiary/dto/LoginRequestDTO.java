package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teamredi.retrodiary.entity.Member;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {

    String username;
    String password;

    @Builder
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Member toEntity(String username, String password) {
        return Member.builder()
                .username(username)
                .password(password)
                .build();
    }
}
