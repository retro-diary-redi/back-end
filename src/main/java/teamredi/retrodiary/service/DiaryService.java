package teamredi.retrodiary.service;

import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamredi.retrodiary.config.AWSS3Config;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.dto.DiaryUpdateRequestDTO;
import teamredi.retrodiary.dto.DiaryWriteRequestDTO;
import teamredi.retrodiary.dto.aws.FileDTO;
import teamredi.retrodiary.entity.Diary;
import teamredi.retrodiary.entity.DiaryImage;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.repository.diary.DiaryRepository;
import teamredi.retrodiary.repository.image.DiaryImageRepository;
import teamredi.retrodiary.repository.member.MemberRepository;
import teamredi.retrodiary.util.DiaryUtils;
import teamredi.retrodiary.util.FileStorageUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private static final Logger log = LoggerFactory.getLogger(DiaryService.class);
    private final MemberRepository memberRepository;

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;

    private final AWSS3Config awss3Config;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;


    /**
     * 다이어리 작성
     *
     * @param diaryWriteRequestDto 저장할 다이어리 정보
     * @param username 다이어리 작성 유저 이름
     **/
    @Transactional
    public void saveDiary(String date, DiaryWriteRequestDTO diaryWriteRequestDto, List<MultipartFile> images, String username) throws IOException {
        log.info("size = " + images.size());
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        Diary createDiary = Diary.createDiary(
                diaryWriteRequestDto.getTitle(),
                diaryWriteRequestDto.getMood(),
                diaryWriteRequestDto.getWeather(),
                diaryWriteRequestDto.getContent(),
                DiaryUtils.stringToLocalDate(date),
                member
                );

        try {
            if (!images.isEmpty() && !images.get(0).isEmpty()) {
                for (MultipartFile image : images) {
                    FileDTO fileDTO = FileStorageUtil.saveFile(image);

                    String originalFilename = fileDTO.getOriginalFilename();
                    String savedFilename = fileDTO.getSavedFilename();
                    File file = fileDTO.getFile();

                    awss3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, savedFilename, file));
                    String awsS3URL = awss3Config.amazonS3Client().getUrl(bucketName, savedFilename).toString();

                    DiaryImage diaryImage =
                            DiaryImage.createDiaryImage(originalFilename, savedFilename, awsS3URL, createDiary);
                    createDiary.addDiaryImages(diaryImage);
                    file.delete();


                }
            }
        } catch (IOException e) {
            // 파일 저장 중 예외가 발생한 경우, 로그를 남기거나 에러 처리를 수행합니다.
            System.err.println("Error saving images: " + e.getMessage());
            throw e; // 예외를 다시 던져 컨트롤러나 상위 레이어에서 처리할 수 있게 합니다.
        }


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
    public void updateDiary(String date, DiaryUpdateRequestDTO diaryUpdateRequestDTO, List<MultipartFile> images, String username) throws IOException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));
        LocalDate localDate = DiaryUtils.stringToLocalDate(date);
        Diary findDiary = diaryRepository.findDiaryByDateAndMember(localDate, member).orElseThrow(() ->
                new NoSuchElementException("해당 날짜에 작성한 다이어리를 찾을 수 없습니다. 작성 날짜 : " + localDate));



        try {
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    FileDTO fileDTO = FileStorageUtil.saveFile(image);

                    String originalFilename = fileDTO.getOriginalFilename();
                    String savedFilename = fileDTO.getSavedFilename();
                    File file = fileDTO.getFile();


                    awss3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, savedFilename, file));
                    String awsS3URL = awss3Config.amazonS3Client().getUrl(bucketName, savedFilename).toString();

                    DiaryImage diaryImage =
                            DiaryImage.createDiaryImage(originalFilename, savedFilename, awsS3URL, findDiary);
                    findDiary.addDiaryImages(diaryImage);


                }
            }
        } catch (IOException e) {
            // 파일 저장 중 예외가 발생한 경우, 로그를 남기거나 에러 처리를 수행합니다.
            System.err.println("Error updating images: " + e.getMessage());
            throw e; // 예외를 다시 던져 컨트롤러나 상위 레이어에서 처리할 수 있게 합니다.
        }

        findDiary.updateDiary(
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

    /**
     * 해당 유저가 작성한 다이어리의 날짜 리스트 조회
     * @param username 다이어리의 날짜를 조회하려는 유저 이름
     **/
    public List<String> getEachUserDiaryDateByUsername(String username) {
        return diaryRepository.getEachUserDiaryDateByUsername(username);
    }





}
