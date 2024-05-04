package teamredi.retrodiary.repository.diary;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import teamredi.retrodiary.dto.DiaryResponseDAO;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.util.FileStorageUtil;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static teamredi.retrodiary.entity.QDiary.*;
import static teamredi.retrodiary.entity.QDiaryImage.*;
import static teamredi.retrodiary.entity.QMember.*;



@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<DiaryResponseDTO> findDiaryByDateAndUsername(LocalDate date, String username) {
        List<DiaryResponseDAO> content = jpaQueryFactory
                .select(Projections.fields(DiaryResponseDAO.class,
                        diary.title,
                        diary.mood,
                        diary.weather,
                        diary.content,
                        member.nickname,
                        diaryImage.savedFilename
                ))
                .from(diary)
                .join(diary.member, member)
                .leftJoin(diary.diaryImages, diaryImage)
                .where(
                        diary.date.eq(date)
                                .and(member.username.eq(username))
                )
                .fetch();

        DiaryResponseDAO diaryResponseDAO = content.get(0);

        List<String> savedFilenames = content.stream()
                .map(DiaryResponseDAO::getSavedFilename)
                .toList();

        return Optional.ofNullable(
                DiaryResponseDTO.builder()
                        .title(diaryResponseDAO.getTitle())
                        .mood(diaryResponseDAO.getMood())
                        .weather(diaryResponseDAO.getWeather())
                        .content(diaryResponseDAO.getContent())
                        .nickname(diaryResponseDAO.getNickname())
                        .savedFilenames(savedFilenames)
                        .build());
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
