package teamredi.retrodiary.repository.diary;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.entity.Diary;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static teamredi.retrodiary.entity.QDiary.*;
import static teamredi.retrodiary.entity.QMember.*;



@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<DiaryResponseDTO> findDiaryByDateAndUsername(LocalDate date, String username) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.fields(DiaryResponseDTO.class,
                        diary.title,
                        diary.mood,
                        diary.weather,
                        diary.content,
                        member.nickname
                ))
                .from(diary)
                .leftJoin(diary.member, member)
                .where(
                        diary.date.eq(date)
                        .and(member.username.eq(username))
                )
                .fetchOne());
    }

    @Override
    public List<String> getEachUserDiaryDateByUsername(String username) {
        List<LocalDate> contents = jpaQueryFactory
                .select(diary.date)
                .from(diary)
                .leftJoin(diary.member, member)
                .where(member.username.eq(username))
                .orderBy(diary.id.asc())
                .fetch();

        return contents.stream()
                        .map(LocalDate::toString)
                        .collect(Collectors.toList());
    }


    @Override
    public boolean existsDiaryByDateAndUsername(LocalDate date, String username) {
        DiaryResponseDTO diaryResponseDTO = jpaQueryFactory
                .select(Projections.fields(DiaryResponseDTO.class,
                        diary.title,
                        diary.mood,
                        diary.weather,
                        diary.content,
                        member.nickname
                ))
                .from(diary)
                .leftJoin(diary.member, member)
                .where(
                        diary.date.eq(date)
                                .and(member.username.eq(username))
                )
                .fetchOne();

        return diaryResponseDTO != null;
    }

}
