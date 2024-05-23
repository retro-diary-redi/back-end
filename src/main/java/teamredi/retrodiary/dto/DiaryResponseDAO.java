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

    private String awsS3SavedFileURL;


    @Builder
    public DiaryResponseDAO(String title, Integer mood, Integer weather, String content, String nickname, String awsS3SavedFileURL) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.nickname = nickname;
        this.awsS3SavedFileURL = awsS3SavedFileURL;
    }
}
