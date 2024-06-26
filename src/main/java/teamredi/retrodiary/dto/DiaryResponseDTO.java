package teamredi.retrodiary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class DiaryResponseDTO {


    private String title;

    private Integer mood;

    private Integer weather;

    private String content;

    private String nickname;

    private List<String> awsS3SavedFileURLs;


    @Builder
    public DiaryResponseDTO(String title, Integer mood, Integer weather, String content, String nickname, List<String> awsS3SavedFileURLs) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.nickname = nickname;
        this.awsS3SavedFileURLs = awsS3SavedFileURLs;
    }
}
