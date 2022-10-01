package com.demoblog.controller;

import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청에 대한 테스트")
    void postSave() throws Exception {

        /**
         * application/json 방식
         */

        //given
        PostForm postForm = PostForm.builder()
                .title("제목입니다.")
                .content("글 내용입니다. 하하하")
                .build();

        String request = convertToJson(postForm);
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

        PostForm postForm = PostForm.builder()
                .title(null)
                .content("글 내용입니다 하하하")
                .build();
        String request = convertToJson(postForm);
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
        PostForm postForm = PostForm.builder()
                .title("제목입니다.")
                .content("글 내용입니다. 하하하")
                .build();
        String request = convertToJson(postForm);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle").value("제목입니다."))
                .andDo(print());

        //then

        Assertions.assertThat(postRepository.count()).isEqualTo(1);

    }

    private String convertToJson(PostForm postForm) throws JsonProcessingException {
        return objectMapper.writeValueAsString(postForm);
    }

}