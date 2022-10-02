package com.demoblog.controller;

import com.demoblog.request.UserForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("유저 회원가입 정보 저장")
    void userSaveTest() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc112")
                .password("Qwertyuiop").build();
        String jsonRequest = convertToJson(userForm);


        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"))
                .andDo(print());

    }

    @Test
    @DisplayName("유저 아이디 없으면 오류 발생")
    void userIdValidationCheck_Null() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .password("Qwertyuiop")
                .build();
        String jsonRequest = convertToJson(userForm);

        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.username").value("회원 아이디는 필수 입니다. (2글자 이상, 20 글자 이하)"))
                .andDo(print());

        //then
    }

    @Test
    @DisplayName("유저 아이디 사이즈 안맞으면 오류 발생")
    void userIdValidationCheck_Size() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("a")
                .password("Qwertyuiop")
                .build();
        String jsonRequest = convertToJson(userForm);

        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.username").value("회원 아이디는 2글자 이상, 20글자 이하입니다."))
                .andDo(print());

    }



    @Test
    @DisplayName("유저 password 없으면 오류 발생")
    void passwordValidationCheck_Null() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc")
                .build();
        String jsonRequest = convertToJson(userForm);

        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.password").value("패스워드는 대,소문자 포함 8글자 이상입니다."))
                .andDo(print()); //test에 대한 request의 자세한 summary 출력
    }

    @Test
    @DisplayName("유저 password 대,소문자 포함 아니면 오류 발생")
    void passwordValidationCheck_WrongCase1() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc")
                .password("qwerty12345") //대,소문자 포함 조건 위반
                .build();
        String jsonRequest = convertToJson(userForm);

        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.password").value("패스워드는 대,소문자 포함 8글자 이상입니다."))
                .andDo(print()); //test에 대한 request의 자세한 summary 출력
    }

    @Test
    @DisplayName("유저 password 8글자 미만이면 오류 발생")
    void passwordValidationCheck_WrongCase2() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc")
                .password("") //8글자 이상 조건 위반
                .build();
        String jsonRequest = convertToJson(userForm);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.password").value("패스워드는 대,소문자 포함 8글자 이상입니다."))
                .andDo(print()); //test에 대한 request의 자세한 summary 출력
    }

    @Test
    @DisplayName("PasswordValidator 유효성 검사")
    void validatorCheckingTest() throws Exception {
        //패스워드는 대,소문자 포함 8글자 이상입니다.
        boolean case1 = isValid("");
        boolean case2 = isValid("Qwerty");
        boolean case3 = isValid("qwerty123");
        boolean case4 = isValid("Qwerty123");

        Assertions.assertThat(case1).isFalse();
        Assertions.assertThat(case2).isFalse();
        Assertions.assertThat(case3).isFalse();
        Assertions.assertThat(case4).isTrue();
    }

    private static boolean isValid(String value) {
        return value.length() >= 8 && value.chars().boxed().anyMatch(Character::isUpperCase);
    }
    private String convertToJson(UserForm userForm) throws JsonProcessingException {
        return objectMapper.writeValueAsString(userForm);
    }
}