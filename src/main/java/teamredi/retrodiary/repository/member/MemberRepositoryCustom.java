package teamredi.retrodiary.repository.member;

import teamredi.retrodiary.dto.MemberResponseDTO;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<MemberResponseDTO> findOneByUsername(String username);
}
