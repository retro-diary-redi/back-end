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


    public void registerMember(JoinRequestDTO requestDto) throws Exception {
        boolean isAlreadyUser = memberRepository.existsByUsername(requestDto.getUsername());

        if (isAlreadyUser) {
            throw new Exception("동일한 아이디가 이미 존재합니다.");
        }

        memberRepository.save(Member.createMember(
                requestDto.getUsername(),
                bCryptPasswordEncoder.encode(requestDto.getPassword()),
                requestDto.getNickname(),
                requestDto.getEmail(),
                Role.USER));

    }
}
