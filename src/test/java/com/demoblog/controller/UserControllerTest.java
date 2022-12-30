package com.demoblog.controller;

import com.demoblog.config.files.AwsS3Config;
import com.demoblog.domain.user.Role;
import com.demoblog.domain.user.User;
import com.demoblog.exception.UserNotFound;
import com.demoblog.repository.UserRepository;
import com.demoblog.request.UserEdit;
import com.demoblog.request.UserForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static com.demoblog.domain.user.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;
    private User user;


    @Test
    @DisplayName("유저 회원가입 정보 저장")
    void userSaveTest() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc112")
                .password("Qwertyuiop")
                .build();
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

        assertThat(case1).isFalse();
        assertThat(case2).isFalse();
        assertThat(case3).isFalse();
        assertThat(case4).isTrue();
    }

    @Test
    @DisplayName("회원가입 username 중복 오류")
    void usernameDuplicateCheck() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("Qwerr123123dfdf")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        UserForm userForm = UserForm.builder()
                .username("su")
                .password("Qwerr123123")
                .build();

        String jsonRequest = convertToJson(userForm);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("중복된 회원 이름입니다."))
                .andDo(print());


        //then
    }

    /**************유저 조회(READ) ***************/
    @Test
    @DisplayName("유저 정보 검색")
    void userFindById() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("12345")
                .role(USER)
                .build();
        userRepository.save(user);

        //when

        //then
    }

    /**************유저 정보 변경(UPDATE) ***************/
    @Test
    @DisplayName("유저 이름 변경")
    void usernameEdit() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("QWEddd12345")
                .role(USER)
                .build();
        userRepository.save(user);

        UserForm userForm = UserForm.builder()
                .username("park")
                .password("QWEddd12345")
                .build();

        String jsonRequest = convertToJson(userForm);
        //when
        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andDo(print());

        User editUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFound());
        assertThat(editUser.getId()).isEqualTo(user.getId());
        assertThat(editUser.getUsername()).isEqualTo(userForm.getUsername());
        //then
    }

    @Test
    @DisplayName("유저 정보 삭제")
    void deleteUser () throws Exception{
        //given
        User user = User.builder()
                .username("su")
                .password("QWEdffdf2345")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        mockMvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(userRepository.count()).isEqualTo(0);
        //then
    }

    @Test
    @DisplayName("유저 인증권한 테스트")
    @WithMockUser
    void authenticatedUser () throws Exception{
        //given
        user = User.builder()
                .username("user")
                .password("user2123123123")
                .role(USER)
                .build();
        userRepository.save(user);

        UserEdit userEdit = UserEdit.builder()
                .username("hello")
                .password("World123123123")
                .build();
        String request = objectMapper.writeValueAsString(userEdit);
        //when
        mockMvc.perform(patch("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print());
        //then
        User ById = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFound());
        assertThat(ById.getUsername()).isEqualTo(userEdit.getUsername());
    }

    private static boolean isValid(String value) {
        return value.length() >= 8 && value.chars().boxed().anyMatch(Character::isUpperCase);
    }

    private String convertToJson(UserForm userForm) throws JsonProcessingException {
        return objectMapper.writeValueAsString(userForm);
    }
}