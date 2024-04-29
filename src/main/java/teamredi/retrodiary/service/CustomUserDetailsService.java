package teamredi.retrodiary.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import teamredi.retrodiary.dto.CustomUserDetails;
import teamredi.retrodiary.dto.MemberResponseDTO;
import teamredi.retrodiary.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberResponseDTO member = memberRepository.findOneByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        if (member != null) {
            return new CustomUserDetails(member);
        }

        return null;
    }
}
