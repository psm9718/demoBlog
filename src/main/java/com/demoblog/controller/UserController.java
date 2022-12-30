package com.demoblog.controller;

import com.demoblog.config.auth.LoginUser;
import com.demoblog.config.auth.dto.SessionUser;
import com.demoblog.domain.user.User;
import com.demoblog.request.UserEdit;
import com.demoblog.request.UserForm;
import com.demoblog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public Map<String, String> createForm(@RequestBody @Valid UserForm userForm) {
        log.info("create user form : {}", userForm);

        Long id = userService.save(userForm);

        return Map.of();

    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("users/{userId}")
    public void get(@PathVariable Long userId, @RequestBody @Valid UserEdit userEdit) {
        userService.modify(userId, userEdit);
    }

    @DeleteMapping("/users/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

//    @GetMapping("/mypage")
//    public String getMypage(Model model, @LoginUser SessionUser sessionUser) {
//        User user = userService.findByEmail(sessionUser.getEmail());
//        model.addAttribute(user);
//        return "/mypage";
//    }

}
