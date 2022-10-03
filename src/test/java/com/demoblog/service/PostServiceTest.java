package com.demoblog.service;

import com.demoblog.domain.Post;
import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostForm;
import com.demoblog.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }
    @Test
    @DisplayName("글 작성")
    void test1() {
        //given
        PostForm postForm = PostForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //when
        postService.write(postForm);

        //then
        assertThat(1L).isSameAs(postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertThat("제목입니다.").isEqualTo(post.getTitle());
        assertThat("내용입니다.").isEqualTo(post.getContent());

    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        //given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertThat(response.getId()).isEqualTo(requestPost.getId());
    }

    /**
     * 서비스 정책에 맞는 클래스
     * - json 응답에서 title 값 길이를 최대 10글자로 해주세요.
     */
    @Test
    @DisplayName("글 1개 조회 (title 10글자 이상)")
    void test4() {
        //given
        Post requestPost = Post.builder()
                .title("서비스에서 요구하는 정책은 엔티티에 만들지 말고 응답 클래스를 분리해야 합니다.")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertThat(response.getId()).isEqualTo(requestPost.getId());
        assertThat(response.getTitle()).isEqualTo("서비스에서 요구하는");
    }
    @Test
    @DisplayName("글 1개 조회 에러 테스트")
    void test3() {
        Long postId = 1L;
        //then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> postService.get(postId));
    }

    @Test
    @DisplayName("글 여러 개 조회")
    void test5 () throws Exception{
        //given
        postRepository.saveAll(List.of(
                Post.builder()
                .title("foo1")
                .content("bar1")
                .build(),
                Post.builder()
                        .title("foo2")
                        .content("bar2")
                        .build(),
                Post.builder()
                        .title("foo3")
                        .content("bar3")
                        .build()
                ));
        //when
        List<PostResponse> posts = postService.getList();

        //then
        assertThat(posts.size()).isEqualTo(3);

    }

}