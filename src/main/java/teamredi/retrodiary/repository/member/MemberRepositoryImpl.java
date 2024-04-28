package teamredi.retrodiary.repository.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import teamredi.retrodiary.dto.MemberResponseDTO;

import java.util.Optional;

import static teamredi.retrodiary.entity.QMember.*;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<MemberResponseDTO> findOneByUsername(String username) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.fields(MemberResponseDTO.class,
                        member.username,
                        member.password,
                        member.nickname,
                        member.email,
                        member.role
                ))
                .from(member)
                .where(member.username.eq(username))
                .fetchOne());
    }

}
