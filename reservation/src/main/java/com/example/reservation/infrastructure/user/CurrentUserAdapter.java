package com.example.reservation.infrastructure.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.reservation.Security.principal.CustomPrincipal;
import com.example.reservation.application.user.CurrentUserport;

@Component
public class CurrentUserAdapter implements CurrentUserport {

  @Override
  public Long getUserId() {
    CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext()
      .getAuthentication().getPrincipal();

    return principal.getUserId();
  }
  
}
