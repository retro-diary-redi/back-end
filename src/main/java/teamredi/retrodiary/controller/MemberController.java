package teamredi.retrodiary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import teamredi.retrodiary.dto.JoinRequestDTO;
import teamredi.retrodiary.dto.JoinResponseDTO;
import teamredi.retrodiaryapp.service.MemberService;

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
}
