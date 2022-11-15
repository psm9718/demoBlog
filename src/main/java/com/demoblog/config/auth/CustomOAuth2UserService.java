package com.demoblog.config.auth;

import com.demoblog.config.auth.dto.OAuthAttributes;
import com.demoblog.config.auth.dto.SessionUser;
import com.demoblog.config.auth.dto.UserProfile;
import com.demoblog.domain.user.User;
import com.demoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //현재 로그인 진행 중인 서비스를 구분하는 코드 (kakao, google, naver ...)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        //OAuth2 로그인 진행 시 키가 되는 필드값 (pk), 구글의 기본 코드는 "sub"

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌

        User user = saveOrUpdate(userProfile);

        httpSession.setAttribute("user", new SessionUser(userProfile));
        //세션에 사용자 정보를 저장하기 위한 DTO SeesionUser을 통해 세션에 저장

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes,
                userNameAttributeName);

    }

    private User saveOrUpdate(UserProfile userProfile) {
        User user= userRepository.findByOauthId(userProfile.getOauthId())
                .map(u -> u.update(userProfile))
                // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update
                .orElse(userProfile.toUser());
        return userRepository.save(user);
    }

}
