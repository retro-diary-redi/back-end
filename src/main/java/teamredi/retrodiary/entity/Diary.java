package teamredi.retrodiary.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"date", "member_id"}
        )
})
@Getter

public class Diary extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer mood;

    @Column(nullable = false)
    private Integer weather;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate date;

    // 양방향
    // 하나의 멤버는 여러개의 다이어리를 작성 할 수 있다.
    @ManyToOne(fetch = FetchType.LAZY) // 연관관계의 주인이다.
    @JoinColumn(name = "member_id")
    private Member member;

    // 양방향
    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "diary")
    private List<DiaryImage> diaryImages = new ArrayList<>();

    @Builder
    public Diary(String title, Integer mood, Integer weather, String content, LocalDate date, Member member) {

        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.date = date;
        this.assignMember(member);
    }

    public static Diary createDiary(String title, Integer mood, Integer weather, String content, LocalDate date, Member member) {
        return Diary.builder()
                .title(title)
                .mood(mood)
                .weather(weather)
                .content(content)
                .date(date)
                .member(member)
                .build();
    }

    // 연관관계 편의 메서드드
    // 연관관계의 주인인 엔티티에 assign 과 같은 연관관계 편의 메서드를 구현
    public void assignMember(Member member) {
        if (this.member != null) {
            this.member.getDiaries().remove(this);
        }
        this.member = member;

        if (!member.getDiaries().contains(this)) {
            member.addDiary(this);
        }
    }

    // 양방향 연관관계 편의 메서드
    public void addDiaryImages(DiaryImage diaryImage) {
        this.diaryImages.add(diaryImage);

        if (diaryImage.getDiary() != this) {
            diaryImage.assignDiary(this);
        }
    }


    public void assignNull() {
        if (!this.diaryImages.isEmpty()) {
            this.diaryImages = new ArrayList<>();
        }
    }


    public void updateDiary(String title, Integer mood, Integer weather, String content) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
    }
}
