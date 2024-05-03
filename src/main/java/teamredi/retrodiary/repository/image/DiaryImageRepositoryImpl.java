package teamredi.retrodiary.repository.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static teamredi.retrodiary.entity.QDiaryImage.diaryImage;

@RequiredArgsConstructor
public class DiaryImageRepositoryImpl implements DiaryImageRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> getSavedFilenameListByDiaryId(Long diaryId) {
        return jpaQueryFactory
                .select(
                        diaryImage.savedFilename)
                .from(diaryImage)
                .where(
                        diaryImage.diary.id.eq(diaryId))
                .fetch();
    }
}
