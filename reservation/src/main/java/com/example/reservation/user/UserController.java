package com.example.reservation.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;





@RestController
@AllArgsConstructor
@ResponseBody
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        userService.registration(userDto);
        
        return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
    }
    
    //사용자 상세 정보
    @GetMapping("/user_detail")
    public ResponseEntity<?> userDetails() throws Exception{
        UserDto userDto = userService.detailUserInfo();

        String body = objectMapper.writeValueAsString(userDto);
        return ResponseEntity.ok(body);
    }
    
    //회원탈퇴
    @DeleteMapping("/user_detail/delete") 
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(Map.of("message", "회원탈퇴 성공"));
    }

    //사용자 정보 변경
    @PutMapping("/user_detail/edit")
    public ResponseEntity<?> userDetailEdit(@RequestBody UserDto userDto) {
        userService.editUser(userDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(Map.of("message", "변경 성공"));
    }

    //이메일 중복 확인
    @PostMapping("/check_email")
    public ResponseEntity<?> checkEmailDuplication(@RequestBody Map<String, String> request) throws Exception {
        String email = request.get("email");
        boolean exists = userService.checkEmailDuplication(email);

        System.out.println(email + " 중복 여부 : " + exists);
        
        if(exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "해당 이메일은 사용 중입니다.", 
                    "available", false));
        } else {
            return ResponseEntity.ok(Map.of("message", "사용 가능한 이메일입니다.", 
                "available", true));
        }
    }
}
