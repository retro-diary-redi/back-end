package teamredi.retrodiary.repository.image;

import java.util.List;

public interface DiaryImageRepositoryCustom {

    List<String> getSavedFilenameListByDiaryId(Long diaryId);
}
