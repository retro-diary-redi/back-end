package teamredi.retrodiary.repository.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamredi.retrodiary.entity.DiaryImage;

@Repository
public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long>, DiaryImageRepositoryCustom{
    void deleteAllByDiary_Id(Long diaryId);
}
