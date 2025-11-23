package com.example.reservation.Security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.reservation.user.User;
import com.example.reservation.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId;
        String username;
        String email;

        //oauth2 제공자에 따른 분기
        switch (provider) {
            case "github":
                providerId = oAuth2User.getAttribute("id");
                username = oAuth2User.getAttribute("login");
                email = "NoEmail";
                break;
            case "naver":
                Map<String, Object> response = oAuth2User.getAttribute("response");
                providerId = (String) response.get("id");
                username = (String) response.get("id");
                email = (String) response.get("email");
            default:
                throw new AssertionError();
        }

        //oauth2 사용자를 찾아보고 없으면 추가(회원가입)
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setProvider(provider);
                newUser.setProviderId(providerId);
                newUser.setUserRole("USER");
                newUser.setEmail(email);
                newUser.setUsername(username);

                return userRepository.save(newUser);
            });

        return new CustomPrincipal(user, oAuth2User.getAttributes(), null, null);
    }
    
}
