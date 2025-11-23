package com.example.reservation.user;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;

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

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    //회원가입
    public User registration(UserDto userDto) {
        User new_user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail());
        new_user.setUserRole("USER");
        new_user.setProvider("local");
        new_user.setProviderId(null);

        return userRepository.save(new_user);
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

    //사용자 정보 변경
    public void editUser(UserDto userDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    //이메일 존재 여부 확인(이메일 중복 확인용)
    public boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

    public Iterable<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User registrationForAdmin(UserDto userDto) {
        User new_user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail());
        new_user.setUserRole("ADMIN");
        new_user.setProvider("local");
        new_user.setProviderId(null);

        return userRepository.save(new_user);
    }
}
