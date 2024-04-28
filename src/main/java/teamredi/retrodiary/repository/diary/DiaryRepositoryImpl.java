package teamredi.retrodiary.repository.diary;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.entity.Diary;


import java.time.LocalDate;
import java.util.Optional;

import static teamredi.retrodiary.entity.QDiary.*;
import static teamredi.retrodiary.entity.QMember.*;



@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<DiaryResponseDTO> findOneById(Long id) {

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
                .fetchOne());
    }

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
