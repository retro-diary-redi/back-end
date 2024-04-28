package teamredi.retrodiary.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseLoginDTO {

    private String username;

    @Builder
    public ResponseLoginDTO(String username, String nickname) {
        this.username = username;
    }
}
