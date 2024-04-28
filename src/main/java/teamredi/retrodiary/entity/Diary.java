package teamredi.retrodiary.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
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
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    @Builder
    public Diary(String title, Integer mood, Integer weather, String content, Member member, LocalDate date) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
        this.assignMember(member);
        this.date = date;
    }

    public static Diary createDiary(String title, Integer mood, Integer weather, String content, Member member, LocalDate date) {
        return Diary.builder()
                .title(title)
                .mood(mood)
                .weather(weather)
                .content(content)
                .member(member)
                .date(date)
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

    public void updateDiary(String title, Integer mood, Integer weather, String content) {
        this.title = title;
        this.mood = mood;
        this.weather = weather;
        this.content = content;
    }
}
