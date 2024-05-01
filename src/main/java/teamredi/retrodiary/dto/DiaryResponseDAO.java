package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DiaryResponseDAO {


    private String title;

    private Integer mood;

    private Integer weather;

    private String content;

    private String nickname;

    private String savedFilename;


    @Builder
    public DiaryResponseDAO(String title, Integer mood, Integer weather, String content, String nickname, String savedFilename) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.nickname = nickname;
        this.savedFilename = savedFilename;
    }
}
