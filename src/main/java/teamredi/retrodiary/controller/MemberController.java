package teamredi.retrodiary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import teamredi.retrodiary.dto.JoinRequestDTO;
import teamredi.retrodiary.dto.JoinResponseDTO;
import teamredi.retrodiary.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
public class MemberController {

    private final MemberService memberService;


    @PostMapping(value = "/auth/registerProc")
    public ResponseEntity<?> registerProcess(@RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("registerProc is working");

        Map<String, Object> responseData = new HashMap<>();
        try {
            memberService.registerMember(joinRequestDTO);
            JoinResponseDTO joinResponseDto =
                    JoinResponseDTO.builder()
                    .username(joinRequestDTO.getUsername())
                    .nickname(joinRequestDTO.getNickname())
                    .build();

            responseData.put("userInfo", joinResponseDto);
            responseData.put("message", "Registration Successful.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("result", "error");
            responseData.put("message", "Registration Fail.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @GetMapping("/auth/status")
    public ResponseEntity<?> isAuthenticated(Authentication authentication) {
        Map<String, Object> responseData = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            responseData.put("status", true);
            responseData.put("message", "session is valid.");
            return ResponseEntity.status(HttpStatus.OK).body(responseData);

        } else {
            responseData.put("status", false);
            responseData.put("message", "session has expired.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        }
    }
}
