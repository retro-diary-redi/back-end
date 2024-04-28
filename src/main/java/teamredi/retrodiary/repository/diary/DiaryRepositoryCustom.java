package teamredi.retrodiary.repository.diary;

import teamredi.retrodiary.dto.DiaryResponseDTO;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepositoryCustom {

    Optional<DiaryResponseDTO> findOneById(Long id);

    Optional<DiaryResponseDTO> findDiaryByDateAndUsername(LocalDate date, String username);

    boolean existsDiaryByDateAndUsername(LocalDate date, String username);

}
