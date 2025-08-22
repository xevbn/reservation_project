package com.example.reservation.Security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.reservation.User;
import com.example.reservation.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) {
        OidcUser user = super.loadUser(oidcUserRequest);

        String provider = oidcUserRequest.getClientRegistration().getClientId();
        String providerId = user.getSubject();
        String email = user.getEmail();
        String name = user.getName();

        userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setProvider(provider);
                newUser.setProviderId(providerId);
                newUser.setEmail(email);
                newUser.setUserRole("USER");
                newUser.setUsername(name);
                newUser.setPassword(null);

                return userRepository.save(newUser);
            });

        return user;
    }
}
