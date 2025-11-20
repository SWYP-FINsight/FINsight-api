package com.company.finsight.global.security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.company.finsight.api.user.entity.Provider;
import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("지원되지 않은 OAuth2 provider 입니다. : " + registrationId);
        }

        return processKakaoUser(oAuth2User);
    }

    private OAuth2User processKakaoUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 사용자 ID
        String providerId = String.valueOf(attributes.get("id"));

        // DB에서 사용자 조회 또는 생성
        User user = userRepository.findByProviderId(providerId)
            .orElseGet(() -> {
                // 새로운 사용자 생성
                String username = "kakao_" + providerId;
                User newUser = User.createOAuthUser(username, Provider.KAKAO, providerId);
                return userRepository.save(newUser);
            });

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
