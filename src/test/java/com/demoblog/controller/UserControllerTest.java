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
    @DisplayName("?????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ????????? ????????? ?????? ??????")
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
                .andExpect(jsonPath("$.errorMessage").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.username").value("?????? ???????????? ?????? ?????????. (2?????? ??????, 20 ?????? ??????)"))
                .andDo(print());

        //then
    }

    @Test
    @DisplayName("?????? ????????? ????????? ???????????? ?????? ??????")
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
                .andExpect(jsonPath("$.errorMessage").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.username").value("?????? ???????????? 2?????? ??????, 20?????? ???????????????."))
                .andDo(print());

    }


    @Test
    @DisplayName("?????? password ????????? ?????? ??????")
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
                .andExpect(jsonPath("$.errorMessage").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.password").value("??????????????? ???,????????? ?????? 8?????? ???????????????."))
                .andDo(print()); //test??? ?????? request??? ????????? summary ??????
    }

    @Test
    @DisplayName("?????? password ???,????????? ?????? ????????? ?????? ??????")
    void passwordValidationCheck_WrongCase1() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc")
                .password("qwerty12345") //???,????????? ?????? ?????? ??????
                .build();
        String jsonRequest = convertToJson(userForm);

        //when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.password").value("??????????????? ???,????????? ?????? 8?????? ???????????????."))
                .andDo(print()); //test??? ?????? request??? ????????? summary ??????
    }

    @Test
    @DisplayName("?????? password 8?????? ???????????? ?????? ??????")
    void passwordValidationCheck_WrongCase2() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("abc")
                .password("") //8?????? ?????? ?????? ??????
                .build();
        String jsonRequest = convertToJson(userForm);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("????????? ???????????????."))
                .andExpect(jsonPath("$.validation.password").value("??????????????? ???,????????? ?????? 8?????? ???????????????."))
                .andDo(print()); //test??? ?????? request??? ????????? summary ??????
    }

    @Test
    @DisplayName("PasswordValidator ????????? ??????")
    void validatorCheckingTest() throws Exception {
        //??????????????? ???,????????? ?????? 8?????? ???????????????.
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
    @DisplayName("???????????? username ?????? ??????")
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
                .andExpect(jsonPath("$.errorMessage").value("????????? ?????? ???????????????."))
                .andDo(print());


        //then
    }

    /**************?????? ??????(READ) ***************/
    @Test
    @DisplayName("?????? ?????? ??????")
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

    /**************?????? ?????? ??????(UPDATE) ***************/
    @Test
    @DisplayName("?????? ?????? ??????")
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
    @DisplayName("?????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ?????????")
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