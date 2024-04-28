package teamredi.retrodiary.repository.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamredi.retrodiary.entity.Diary;
import teamredi.retrodiary.entity.Member;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
    Optional<Diary> findDiaryById(Long id);


    void deleteDiaryById(Long id);

    Optional<Diary> findDiaryByDateAndMember(LocalDate date, Member member);
}
