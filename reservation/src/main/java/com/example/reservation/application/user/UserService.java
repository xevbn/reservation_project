package com.example.reservation.application.user;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.user.UserDomain;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDomain findByUsername(String username) {
        UserDomain domain = userRepository.findByUsername(username);

        if(domain == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return domain;
    }

    public UserDomain findById(Long id) {
        UserDomain domain = userRepository.findById(id);

        if(domain == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return domain;
    }

    //회원가입
    public UserDomain registration(String email, String username, String password) {
        if(userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserDomain domain = UserDomain.builder()
            .email(email)
            .username(username)
            .password(passwordEncoder.encode(password))
            .provider("local")
            .providerId("null")
            .build();

        return userRepository.save(domain);
    }

    //사용자 상세 정보 페이지
    public UserDomain detailUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDomain found = userRepository.findByUsername(username);

        if(found == null)    
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. " + username);
        
        return found;
    }

    //삭제에 필수
    @Transactional
    //회원탈퇴
    public void deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.deleteByUsername(username);
    }

    //사용자 정보 변경
    public void editUser(String newuUsername, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDomain found = userRepository.findByUsername(username);
        
        if(found == null)
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        
        found.changeUsername(username);
        found.changePassword(newPassword);

        userRepository.save(found);
    }

    //이메일 존재 여부 확인(이메일 중복 확인용)
    public boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserDomain> listAllUsers() {
        return userRepository.findAll();
    }

    public UserDomain registrationForAdmin(String email, String username, String password) {
        UserDomain newUser = UserDomain.builder()
            .email(email).username(username)
            .password(passwordEncoder.encode(password))
            .provider("Local")
            .providerId("null")
            .userRole("ADMIN")
            .build();

        return userRepository.save(newUser);
    }
}
