package teamredi.retrodiary.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import teamredi.retrodiary.dto.ResponseLoginDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> responseData = new HashMap<>();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("success handler is working");
        UserDetails user =  (UserDetails) authentication.getPrincipal();
        log.info("login user name : " + user.getUsername());


        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResponseLoginDTO responseLoginDTO =
                ResponseLoginDTO.builder()
                                .username(user.getUsername())
                                .build();
        responseData.put("userInfo", responseLoginDTO.getUsername());
        responseData.put("message", "Authentication Successful.");

        objectMapper.writeValue(response.getWriter(), responseData);
    }
}
