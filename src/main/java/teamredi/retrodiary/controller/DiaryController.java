package teamredi.retrodiary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teamredi.retrodiary.dto.DiaryResponseDTO;
import teamredi.retrodiary.dto.DiaryUpdateRequestDTO;
import teamredi.retrodiary.dto.DiaryWriteRequestDTO;
import teamredi.retrodiary.dto.oauth2.CustomOAuth2User;
import teamredi.retrodiary.service.DiaryImageService;
import teamredi.retrodiary.service.DiaryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    private final DiaryImageService diaryImageService;

//    @Secured("USER")
    @GetMapping("/diaries/write")
    public ResponseEntity<?> showDiary() {

        return new ResponseEntity<>("DiaryWritePage", HttpStatus.OK);
    }

    /**
     * 다이어리 작성
     *
     * @param diaryWriteRequestDTO 작성할 다이어리 정보
     * @param multipartFile 다이어리 업로드 이미지 정보
     * @param authentication 요청한 유저의 정보
     * @return 클라이언트로 응답
     **/
    @Secured("USER")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/diaries/{date}/write")
    public ResponseEntity<?> writeDiary(@PathVariable String date,
                                        @RequestPart(value = "diaryWriteRequestDTO") DiaryWriteRequestDTO diaryWriteRequestDTO,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                        Authentication authentication) {
        log.info("diary write is working");


//        // 이놈 때문에 올바른 사용자의 정보를 가져오지 못했다.
//        if (authentication instanceof UsernamePasswordAuthenticationToken) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            username = userDetails.getUsername();
//        }
        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }
        log.info("username = " + username);

        Map<String, Object> responseData = new HashMap<>();
        try {
            diaryService.saveDiary(date, diaryWriteRequestDTO, multipartFile,  username);
            responseData.put("message", "일기를 성공적으로 작성했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", "일기 형식이 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);

    }


    /**
     * 다이어리 조회
     *
     * @param date 다이어리 작성 날짜
     * @param authentication 요청한 유저의 정보
     * @return 클라이언트로 응답
     **/
    @Secured("USER")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diaries/{date}")
    public ResponseEntity<?> findDiary(@PathVariable String date, Authentication authentication) {
        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        Map<String, Object> responseData = new HashMap<>();

        try {
            DiaryResponseDTO diary = diaryService.getDiaryByDateAndUsername(date, username);
            responseData.put("diaryInfo", diary);
            responseData.put("message", "일기를 성공적으로 찾았습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", "일기를 찾지 못하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }


    /**
     * 다이어리 업데이트(GET)
     *
     * @param date 다이어리 번호
     * @param authentication 요청한 유저의 정보
     * @return 클라이언트로 응답
     **/
    @Secured("USER")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diaries/{date}/update")
    public ResponseEntity<?> updateDiary(@PathVariable String date, Authentication authentication) {
        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        Map<String, Object> responseData = new HashMap<>();

        try {
            DiaryResponseDTO diaryResponseDTO = diaryService.getDiaryByDateAndUsername(date, username);
            responseData.put("diaryInfo", diaryResponseDTO);
            responseData.put("message", "Find Update Diary Successful");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", "Find Update Diary Fail.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseData);

    }


    /**
     * 다이어리 업데이트(PUT)
     *
     * @param date 다이어리 작성 날짜
     * @param authentication 요청한 유저의 정보
     * @return 클라이언트로 응답
     **/

    @Secured("USER")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/diaries/{date}/update")
    public ResponseEntity<?> updateDiary(@PathVariable String date,
                                         @RequestPart(value = "diaryUpdateRequestDTO") DiaryUpdateRequestDTO diaryUpdateRequestDTO,
                                         @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                         Authentication authentication) {
        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        Map<String, Object> responseData = new HashMap<>();

        try {
            diaryImageService.deleteDiaryImageFromDBAndLocalStorage(date, username);
            diaryService.updateDiary(date, diaryUpdateRequestDTO, multipartFile, username);
            responseData.put("message", "일기를 성공적으로 업데이트 했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", "일기 형식이 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    /**
     * 게시글 삭제
     * @param date 다이어리 작성 날짜
     * @param authentication 유저 정보
     * @return 클라이언트로 응답
     */
    @Secured("USER")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/diaries/{date}/delete")
    public ResponseEntity<?> deleteDiary(@PathVariable String date, Authentication authentication) {
//        BoardResponseDTO result = boardService.boardDetail(id);
//        if (result.getEmail() != userDetails.getUsername()) {
//            return "redirect:/";
//        }

        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        Map<String, Object> responseData = new HashMap<>();

        try {
            diaryImageService.deleteDiaryImageFromLocalStorage(date, username);
            diaryService.deleteDiary(date, username);

            responseData.put("message", "다이어리를 성공적으로 삭제 했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", "다이어리 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }


}
