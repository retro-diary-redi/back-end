package teamredi.retrodiary.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamredi.retrodiary.enumerate.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 앱에서 활동할 이름
    @Column(nullable = false)
    private String nickname;

    // 이메일
    @Column(nullable = false)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    // 하나의 멤버는 여러개의 다이어리를 작성 할 수 있다.
    // 양방향
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Diary> diaries = new ArrayList<>();

    @Builder
    public Member(String username, String password, String nickname, String email, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public static Member createMember(String username, String password, String nickname, String email, Role role) {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .role(role)
                .build();
    }

    public void assignEmail(String email) {
        this.email = email;
    }

    // 양방향 연관관계 편의 메서드
    public void addDiary(Diary diary) {
        this.diaries.add(diary);

        if (diary.getMember() != this) {
            diary.assignMember(this);
        }
    }
}
