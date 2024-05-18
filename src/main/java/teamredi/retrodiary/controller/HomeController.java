package teamredi.retrodiary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import teamredi.retrodiary.dto.oauth2.CustomOAuth2User;
import teamredi.retrodiary.service.DiaryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final DiaryService diaryService;

    @GetMapping("/diaries")
    public ResponseEntity<?> homePage(Authentication authentication) {
        Map<String, Object> responseData = new HashMap<>();
        List<String> diaryDateList = new ArrayList<>();
        if (authentication == null) {
            responseData.put("counts", 0);
            responseData.put("diaryDateList", diaryDateList);
            responseData.put("message", "로그인되지 않은 사용자의 요청입니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseData);

            // 그외 사용자
        } else {
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

            diaryDateList = diaryService.getEachUserDiaryDateByUsername(username);
            responseData.put("diaryDateList", diaryDateList);
            responseData.put("counts", diaryDateList.size());
            responseData.put("message", "로그인된 사용자의 요청입니다.");

            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        }
    }

}
