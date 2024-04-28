package teamredi.retrodiary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.dto.DiaryUpdateRequestDTO;
import teamredi.retrodiary.dto.DiaryWriteRequestDTO;
import teamredi.retrodiary.entity.Diary;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.repository.diary.DiaryRepository;
import teamredi.retrodiary.repository.member.MemberRepository;
import teamredi.retrodiary.util.DiaryUtils;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final MemberRepository memberRepository;

    private final DiaryRepository diaryRepository;

    /**
     * 다이어리 작성
     *
     * @param diaryWriteRequestDto 저장할 다이어리 정보
     * @param username 다이어리 작성 유저 이름
     **/
    @Transactional
    public void saveDiary(DiaryWriteRequestDTO diaryWriteRequestDto, String date, String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        Diary createDiary = Diary.createDiary(
                diaryWriteRequestDto.getTitle(),
                diaryWriteRequestDto.getMood(),
                diaryWriteRequestDto.getWeather(),
                diaryWriteRequestDto.getContent(),
                member,
                DiaryUtils.stringToLocalDate(date));

        diaryRepository.save(createDiary);
    }

    /**
     * 다이어리 조회(단건)
     *
     * @param date 다이어리 작성 날짜
     * @param username 다이어리 작성 유저 이름
     * @return 해당하는 다이어리 정보
     **/
    public DiaryResponseDTO getDiaryByDateAndUsername(String date, String username) {
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        return diaryRepository.findDiaryByDateAndUsername(
                DiaryUtils.stringToLocalDate(date), username)
                .orElseThrow(() ->
                new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate));
    }

    /**
     * 다이어리 수정
     *
     * @param date 다이어리
     * @param username 다이어리 작성 유저 이름
     *
     * @param diaryUpdateRequestDTO 다이어리 변경 데이터
     **/
    @Transactional
    public void updateDiary(String date, String username, DiaryUpdateRequestDTO diaryUpdateRequestDTO) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        Diary diary = diaryRepository.findDiaryByDateAndMember(localDate, member).orElseThrow(() ->
                new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate));

        diary.updateDiary(
                diaryUpdateRequestDTO.getTitle(),
                diaryUpdateRequestDTO.getMood(),
                diaryUpdateRequestDTO.getWeather(),
                diaryUpdateRequestDTO.getContent());
    }

    /**
     * 다이어리 삭제
     *
     * @param date 다이어리 작성 날짜
     * @param username 다이어리 작성 유저 이름
     *
     **/
    @Transactional
    public void deleteDiary(String date, String username) {
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        boolean isExistsDiary = diaryRepository.existsDiaryByDateAndUsername(
                localDate, username);
        if (isExistsDiary) {
            Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));
            diaryRepository.deleteDiaryByDateAndMember(localDate, member);
        } else {
            throw new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate);
        }
    }





}
