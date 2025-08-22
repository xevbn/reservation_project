package com.example.reservation;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //회원가입
    public void registration(UserDto userDto) {
        User new_user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail());
        new_user.setUserRole("USER");
        new_user.setProvider(null);
        new_user.setProviderId(null);

        userRepository.save(new_user);
    }

    //사용자 상세 정보 페이지
    public UserDto detailUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. " + username));
        
        UserDto userDto = new UserDto(user.getUsername(), null, user.getEmail());
        return userDto;
    }

    //삭제에 필수
    @Transactional
    //회원탈퇴
    public void deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.deleteByUsername(username);
    }
}
