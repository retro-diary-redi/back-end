package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DiaryResponseDTO {


    private String title;

    private Integer mood;

    private Integer weather;

    private String content;

    private String nickname;


    @Builder
    public DiaryResponseDTO(String title, Integer mood, Integer weather, String content, String nickname) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.nickname = nickname;
    }
}
