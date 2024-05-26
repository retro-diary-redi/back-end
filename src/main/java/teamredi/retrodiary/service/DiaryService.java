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
            if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
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
            if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
                for (MultipartFile image : images) {
                    FileDTO fileDTO = FileStorageUtil.saveFile(image);

                    String originalFilename = fileDTO.getOriginalFilename();
                    String savedFilename = fileDTO.getSavedFilename();
                    File file = fileDTO.getFile();

                    // 업데이트 하려는 이미지가 이미 버킷에 존재한다면 따로 버켓 저장 요청을 보내지 않고 다음 이미지를 확인한다.
                    // 하지만 버킷에서 파일을 서버로 가져오는 것은 요금이 많이 부과됨으로 getObject() 메서드 활용을 최대한 사용을 자제해야한다.
                    // 그래서 doseObjectExist() 메서드를 활용하여 이미 버킷내에 해당 파일이 존재한다면 새로 저장할 필요없이 다음 루프를 실행한다.
//                    if (awss3Config.amazonS3Client().doesObjectExist(bucketName, savedFilename)) {
//                        continue;
//                    }
                    awss3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, savedFilename, file));
                    String awsS3URL = awss3Config.amazonS3Client().getUrl(bucketName, savedFilename).toString();

                    DiaryImage diaryImage =
                            DiaryImage.createDiaryImage(originalFilename, savedFilename, awsS3URL, findDiary);
                    findDiary.addDiaryImages(diaryImage);

                    file.delete();

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
