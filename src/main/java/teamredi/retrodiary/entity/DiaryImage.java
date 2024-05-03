package teamredi.retrodiary.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor
@Getter
public class DiaryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원본 파일명
    @Column(nullable = false)
    private String originalFilename;

    // 이미지에 대한 정보를 DB에서 찾을때 활용
    @Column(nullable = false)
    private String savedFilename;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Builder
    public DiaryImage(String originalFilename, String savedFilename, Diary diary) {
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.assignDiary(diary);
    }

    public static DiaryImage createDiaryImage(String originalFilename, String savedFilename, Diary diary) {
        return DiaryImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .diary(diary)
                .build();
    }

    // 연관관계 편의 메서드드
    // 연관관계의 주인인 엔티티에 assign 과 같은 연관관계 편의 메서드를 구현
    public void assignDiary(Diary diary) {
        if (this.diary != null) {
            this.diary.getDiaryImages().remove(this);
        }
        this.diary = diary;

        if (!diary.getDiaryImages().contains(this)) {
            diary.addDiaryImages(this);
        }
    }

//    public void updateImagePath(String imagePath) {
//        this.imagePath = imagePath;
//    }




}
