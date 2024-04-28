package teamredi.retrodiary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DiaryResponseDTO {


    private String title;

    private String mood;

    private String weather;

    private String content;

    private String nickname;


    public DiaryResponseDTO(String title, String mood, String weather, String content, String nickname) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.nickname = nickname;
    }
}
