package com.example.reservation.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.reservation.application.user.UserRepository;
import com.example.reservation.application.user.UserService;
import com.example.reservation.domain.user.UserDomain;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
  @InjectMocks
  UserService userService;
  @Mock
  UserRepository userRepository;

  @Test
  @DisplayName("회원가입 테스트")
  void registration_test() {
    UserDomain user = new UserDomain(1L, "email", "test", "password", "USER");
    when(userRepository.existsByEmail(any())).thenReturn(false);
    when(userRepository.save(any())).thenReturn(user);

    userService.registration("email", "test", "password");

    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("회원가입 시 이메일 중복 테스트")
  void registration_email_duplication_test() {
    when(userRepository.existsByEmail(any())).thenReturn(true);

    try {
      userService.registration("email", "test", "password");
    } catch (Exception e) {
      assert(e.getMessage().equals("이미 존재하는 이메일입니다."));
    }
  }

  @Test
  @DisplayName("사용자 조회 테스트")
  void findByUsername_test() {
    UserDomain user = new UserDomain(2L, "email2", "test2", "password2", "USER");
    when(userRepository.findByUsername(any())).thenReturn(user);

    UserDomain rs = userService.findByUsername("test2");
    verify(userRepository).findByUsername(any());
    
    assert(rs.getId() == 2L);
  }

  @Test
  @DisplayName("사용자 조회 시 존재하지 않는 사용자 조회 테스트")
  void findByUsername_not_found_test() {
    when(userRepository.findByUsername(any())).thenReturn(null);

    try {
      userService.findByUsername("nonexistent");
      verify(userRepository).findByUsername(any());
    } catch (Exception e) {
      assert(e.getMessage().equals("사용자를 찾을 수 없습니다."));
    }
  }
}
