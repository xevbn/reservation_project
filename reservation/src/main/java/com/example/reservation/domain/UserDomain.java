package com.example.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDomain {
  private long id;
  private String email;
  private String username;
  private String password;
  private String userRole;
  private String provider;
  private String providerId;

  public UserDomain(String email, String username, String password, String userRole) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.userRole = userRole;
    this.provider = "LOCAL";
    this.providerId = "null";
  }

  public static UserDomain createAdmin(String email, String username, String password) {
    return new UserDomain(email, username, password, "ADMIN");
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void changeUsername(String newUsername) {
    this.username = newUsername;
  }
}
