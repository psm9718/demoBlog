package com.demoblog.controller;

import com.demoblog.request.PostForm;
import com.demoblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// SSR -> jsp, thymeleaf
//      : html rendering
// SPA ->
//      vue -> vue + SSR = nuxt
//      react -> react + SSR = next
//      : javascript + <-> API (JSON)

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    //http Method
    //GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT

    private final PostService postService;

    //글 등록 : POST
    @GetMapping("/posts")
    public String postCreate() {
        log.info("post create page");
        return "postCreate";
    }
    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostForm postForm) {
        //클라이언트에게 저장한 해당 객체의 PK를 Map 형태로 전달
        log.info("postForm = {}", postForm);

        String postTitle = postService.write(postForm);
        return Map.of("postTitle", postTitle);
    }

}
