package com.example.reservation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
@ResponseBody
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/register")
    public String register(UserDto userDto) {
        userService.registration(userDto);
        
        return "redirect:/login";
    }
    
    //사용자 상세 정보
    @GetMapping("/user_detail")
    public UserDto userDetails() {
        UserDto userDto = userService.detailUserInfo();

        return userDto;
    }
    
    //회원탈퇴
    @DeleteMapping("/user_detail/delete") 
    public String deleteUser() {
        userService.deleteUser();

        return "redirect:/login";
    }
}
