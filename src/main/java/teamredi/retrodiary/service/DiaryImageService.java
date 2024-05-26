package teamredi.retrodiary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamredi.retrodiary.config.AWSS3Config;
import teamredi.retrodiary.entity.Diary;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.repository.diary.DiaryRepository;
import teamredi.retrodiary.repository.image.DiaryImageRepository;
import teamredi.retrodiary.repository.member.MemberRepository;
import teamredi.retrodiary.util.DiaryUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryImageService {

    private final DiaryRepository diaryRepository;

    private final MemberRepository memberRepository;

    private final DiaryImageRepository diaryImageRepository;

    private final AWSS3Config awss3Config;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public void deleteDiaryImageFromDB(String date, String username) {

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        Diary diary = diaryRepository.findDiaryByDateAndMember(localDate, member).orElseThrow(() ->
                new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate));
        Long diaryId = diary.getId();
        List<String> savedFilenameList = diaryImageRepository.getSavedFilenameListByDiaryId(diaryId);


        try {
            if (savedFilenameList != null && !savedFilenameList.isEmpty() && !savedFilenameList.get(0).isEmpty()) {
                for (String savedFilename : savedFilenameList) {
                    awss3Config.amazonS3Client().deleteObject(bucketName, savedFilename);

                }
            }
        } catch (Exception e) {
            // 파일 저장 중 예외가 발생한 경우, 로그를 남기거나 에러 처리를 수행합니다.
            System.err.println("Error deleting images: " + e.getMessage());
            throw e; // 예외를 다시 던져 컨트롤러나 상위 레이어에서 처리할 수 있게 합니다.
        }
        // 해당 다이어리에 엔티티와 연관관계가 있는 자식 다이어리 이미지 엔티티 DB에서 삭제
        diaryImageRepository.deleteAllByDiary(diary);

    }


}
