package teamredi.retrodiary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamredi.retrodiary.entity.Diary;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.repository.diary.DiaryRepository;
import teamredi.retrodiary.repository.image.DiaryImageRepository;
import teamredi.retrodiary.repository.member.MemberRepository;
import teamredi.retrodiary.util.DiaryUtils;
import teamredi.retrodiary.util.FileStorageUtil;

import java.io.IOException;
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

    @Transactional
    public void deleteDiaryImageFromLocalStorage(String date, String username) throws IOException {

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        Diary diary = diaryRepository.findDiaryByDateAndMember(localDate, member).orElseThrow(() ->
                new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate));
        Long diaryId = diary.getId();
        List<String> savedFilenameList = diaryImageRepository.getSavedFilenameListByDiaryId(diaryId);

        FileStorageUtil.deleteFile(savedFilenameList);
    }

}
