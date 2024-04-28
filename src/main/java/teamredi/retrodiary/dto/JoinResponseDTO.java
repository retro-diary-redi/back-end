package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinResponseDTO {

    // 이메일
    private String username;

    private String nickname;

    @Builder
    public JoinResponseDTO(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }
}
