package com.demoblog.controller;

import com.demoblog.domain.Post;
import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostCreate;
import com.demoblog.request.PostEdit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청에 대한 테스트")
    void postSave() throws Exception {

        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다. 하하하")
                .build();

        String request = convertToJson(postCreate);
        System.out.println("request = " + request);

        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle").value("제목입니다."))
                .andDo(print()); //test에 대한 request의 자세한 summary 출력

    }


    @Test
    @DisplayName("/posts 요청시 title 값은 필수다.")
    void titleValidationCheck() throws Exception {

        PostCreate postCreate = PostCreate.builder()
                .title(null)
                .content("글 내용입니다 하하하")
                .build();
        String request = convertToJson(postCreate);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(request)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀 값은 필수입니다."))
                .andDo(print()); //test에 대한 request의 자세한 summary 출력
    }

    @Test
    @DisplayName("/posts 요청 시 DB에 포스트가 저장된다.")
    void postSave_db() throws Exception {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다. 하하하")
                .build();
        String request = convertToJson(postCreate);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle").value("제목입니다."))
                .andDo(print());

        //then

        assertThat(postRepository.count()).isEqualTo(1);

    }

    @Test
    @DisplayName("글 1개 조회")
    void findOne() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);
        //expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회 (타이틀 10글자 이상)")
    void findOne_LongTitle() throws Exception {
        //given
        Post post = Post.builder()
                .title("서비스에서 요구하는 정책은 엔티티에 만들지 말고 응답 클래스를 분리해야 합니다.")
                .content("bar")
                .build();
        postRepository.save(post);

        //expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle().substring(0, 10)))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 1 페이지 조회")
    void findList() throws Exception {
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


        /**
         * list 형태로 object들이 담겨서 응답됨
         * [ {"id" : ..., "title" : ...} , {id : .., title : ..}]
         */
        //expected
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("제목 (29)"))
                .andExpect(jsonPath("$[0].content").value("내용입니다. 29"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 내용 수정")
    void editContent() throws Exception {
        //given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .content("내용이에요 호호")
                .build();

        String request = objectMapper.writeValueAsString(postEdit);

        //when
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePosts() throws Exception {
        //given
        Post post = Post.builder()
                .title("글 제목")
                .content("글 내용")
                .build();
        postRepository.save(post);

        //when
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 게시글")
    void postNotFound() throws Exception {
        //given
        mockMvc.perform(get("/posts/{postId}", 33L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //when


        //then
    }

    /**
     * 수정 필요! json 컨버팅 해주는 별도의 객체 구현
     *
     * @param postCreate
     * @return
     * @throws JsonProcessingException
     */
    private String convertToJson(PostCreate postCreate) throws JsonProcessingException {
        return objectMapper.writeValueAsString(postCreate);
    }

}