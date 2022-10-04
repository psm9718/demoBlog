package com.demoblog.service;

import com.demoblog.domain.Post;
import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostEdit;
import com.demoblog.request.PostForm;
import com.demoblog.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(IllegalArgumentException.class,
                () -> postService.get(postId));
    }

    @Test
    @DisplayName("글 1 페이지 조회")
    void test5() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("제목 (" + i + ")")
                            .content("내용입니다. " + i)
                            .build();
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);


        //when
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        List<PostResponse> posts = postService.getList(pageable);

        //then
        assertThat(posts.size()).isEqualTo(5);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목 (29)");
        assertThat(posts.get(4).getTitle()).isEqualTo("제목 (25)");
    }

    @Test
    @DisplayName("글 제목 수정")
    void editTitle () throws Exception{
        //given
        Post post = Post.builder()
                .title("제목")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("안녕하십니까")
                .content("내용입니다.")
                .build();
        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changePost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id =" + post.getId()));

        assertThat(changePost.getTitle()).isEqualTo("안녕하십니까");
        assertThat(changePost.getContent()).isEqualTo("내용입니다.");
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost () throws Exception{
        //given
        Post post = Post.builder()
                .title("제목")
                .content("내용입니다.")
                .build();
        postRepository.save(post);


        //when
        postService.delete(post.getId());

        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }

}