package teamredi.retrodiary.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import teamredi.retrodiary.dto.JoinRequestDTO;
import teamredi.retrodiary.entity.Member;
import teamredi.retrodiary.enumerate.Role;
import teamredi.retrodiary.repository.member.MemberRepository;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public Long registerMember(JoinRequestDTO requestDto) {
        boolean isAlreadyUser = memberRepository.existsByUsername(requestDto.getUsername());

        if (isAlreadyUser) {
            return 0L;
        }

        return memberRepository.save(Member.createMember(
                requestDto.getUsername(),
                bCryptPasswordEncoder.encode(requestDto.getPassword()),
                requestDto.getNickname(),
                requestDto.getEmail(),
                Role.USER)).getId();
    }
}
