package jieyi.lu.huanju.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    /*
    下面方法等价于这么写：
    @GetMapping("/me")
    public String getCurrentUser() {
        // 传统写法：手动从SecurityContext取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return "当前登录用户: " + userDetails.getUsername();
    }
    */
    @GetMapping("/me")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) { // AuthenticationPrincipal 表示从 SecurityContext 中获取当前登录用户（UserDetails对象）
        return "当前登录用户: " + userDetails.getUsername();
    }
}