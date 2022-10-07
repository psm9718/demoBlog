package com.demoblog.controller;

import com.demoblog.request.UserForm;
import com.demoblog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public Map<String, String> createForm(@RequestBody @Valid UserForm userForm) {
        log.info("create user form : {}",userForm);

        Long id = userService.save(userForm);

        return Map.of();

    }

    /**
     * 회원 정보 조회
     */
    @PatchMapping("users/{userId}")
    public void get(@PathVariable Long userId) {
        log.info("READ user info");
    }


}
