package com.demoblog.controller;

import com.demoblog.config.auth.LoginUser;
import com.demoblog.config.auth.dto.SessionUser;
import com.demoblog.service.PostService;
import com.demoblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostService postService;

    @RequestMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postService.findAllDesc());
        if (user != null) {
            model.addAttribute("username", user.getName());
        }
        return "index";
    }
}
