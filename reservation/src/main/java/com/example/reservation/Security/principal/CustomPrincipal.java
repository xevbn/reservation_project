package com.example.reservation.Security.principal;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.reservation.Security.CustomUserDetails;
import com.example.reservation.infrastructure.user.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomPrincipal implements OAuth2User, OidcUser {
    private final CustomUserDetails userDetails;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo oidcUserInfo;


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }

    public String getEmail() {
        return userDetails.getEmail();
    }

    public User getUser() {
        return userDetails.getUser();
    }

    public Long getUserId() {
        return userDetails.getId();
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUserInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
