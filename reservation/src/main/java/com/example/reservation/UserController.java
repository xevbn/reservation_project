package com.example.reservation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/register")
    public String register(UserDto userDto) {
        userService.registration(userDto);
        
        return "redirect:/login";
    }
    
}
