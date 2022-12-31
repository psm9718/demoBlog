package com.demoblog.controller;

import com.demoblog.request.PostEdit;
import com.demoblog.request.PostCreate;
import com.demoblog.request.PostSearch;
import com.demoblog.response.PostResponse;
import com.demoblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.http.HttpRequest;
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

    private final PostService postService;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        log.info(">> {}", request.getParameter("accessToken"));
        return "hello";
    }

    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostCreate postCreate) {
        //클라이언트에게 저장한 해당 객체의 PK를 Map 형태로 전달
        postCreate.validate();
        String postTitle = postService.write(postCreate);
        return Map.of("postTitle", postTitle);
    }

    @GetMapping("/posts/{postId}") //단건 조회 api
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts") //한 페이지 post 조회 api
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
        postService.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }


}
