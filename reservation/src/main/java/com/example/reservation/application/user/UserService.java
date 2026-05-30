package com.example.reservation.application.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.UserDomain;
import com.example.reservation.presentation.user.dto.UserDto;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserDomain> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserDomain> findById(Long id) {
        return userRepository.findById(id);
    }

    //회원가입
    public UserDomain registration(String email, String username, String password) {
        UserDomain newUser = new UserDomain(
            email,
            username, 
            passwordEncoder.encode(password),
            "USER"
        );
        UserDomain user = userRepository.save(newUser);

        log.info("User [계정 생성] - userId: {}, provider: {}", user.getId(), user.getProvider());

        return user;
    }

    //사용자 상세 정보 페이지
    public UserDto detailUserInfo(long userId) {
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. " + userId));
        
        UserDto userDto = new UserDto(user.getUsername(), null, user.getEmail());
        return userDto;
    }

    //삭제에 필수
    @Transactional
    //회원탈퇴
    public void deleteUser(long userId) {
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        userRepository.deleteById(userId);
        log.info("User [계정 삭제] - userId: {}", user.getId());
    }

    //사용자 정보 변경
    public void editUser(long userId, String username, String password) {
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.changeUsername(username);
        user.changePassword(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("User [계정 정보 변경] - userId: {}", user.getId());
    }

    //이메일 존재 여부 확인(이메일 중복 확인용)
    public boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserDomain> listAllUsers() {
        return userRepository.findAll();
    }

    public UserDomain registrationForAdmin(String email, String username, String password) {
        UserDomain newUser = UserDomain.createAdmin(email, username, password);

        return userRepository.save(newUser);
    }
}
