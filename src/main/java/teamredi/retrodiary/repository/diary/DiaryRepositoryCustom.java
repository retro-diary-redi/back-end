package teamredi.retrodiary.repository.diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import teamredi.retrodiary.dto.DiaryResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepositoryCustom {

    Optional<DiaryResponseDTO> findDiaryByDateAndUsername(LocalDate date, String username);

    List<String> getEachUserDiaryDateByUsername(String username);

    boolean existsDiaryByDateAndUsername(LocalDate date, String username);



}
