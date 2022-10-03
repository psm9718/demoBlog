package com.demoblog.controller;

import com.demoblog.domain.Post;
import com.demoblog.request.PostForm;
import com.demoblog.response.PostResponse;
import com.demoblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostForm postForm) {
        //클라이언트에게 저장한 해당 객체의 PK를 Map 형태로 전달
        log.info("postForm = {}", postForm);

        String postTitle = postService.write(postForm);
        return Map.of("postTitle", postTitle);
    }

    /**
     * /posts -> 글 전체 조회(검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     */

    @GetMapping("/posts/{postId}") //단건 조회 api
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts") //여러 post 조회 api
    public List<PostResponse> getList() {
        return postService.getList();
    }



}
