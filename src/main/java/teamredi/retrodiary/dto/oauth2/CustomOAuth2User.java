package teamredi.retrodiary.dto.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import teamredi.retrodiary.enumerate.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserInfo oAuth2UserInfo;

    private final Role role;

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) role::getValue);

        return collection;
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getName();
    }

    public String getUsername() {
        return oAuth2UserInfo.getProvider() + " " + oAuth2UserInfo.getProviderId();
    }
}
