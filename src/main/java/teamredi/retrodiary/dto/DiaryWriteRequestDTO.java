package teamredi.retrodiary.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiaryWriteRequestDTO {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer mood;

    @Column(nullable = false)
    private Integer weather;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String date;

    @Builder
    public DiaryWriteRequestDTO(String title, Integer mood, Integer weather, String content, String date) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.date = date;
    }
}
