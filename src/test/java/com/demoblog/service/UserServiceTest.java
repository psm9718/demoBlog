package com.demoblog.service;

import com.demoblog.domain.user.Role;
import com.demoblog.domain.user.User;
import com.demoblog.exception.UserNotFound;
import com.demoblog.repository.UserRepository;
import com.demoblog.request.UserEdit;
import com.demoblog.request.UserForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static com.demoblog.domain.user.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("사용자 회원가입")
    void saveUser() throws Exception {
        //given
        UserForm userForm = UserForm.builder()
                .username("su")
                .password("123")
                .build();

        //when
        userService.save(userForm);

        //then
        User user = userRepository.findAll().get(0);
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(user.getUsername()).isEqualTo("su");
    }

    @Test
    @DisplayName("사용자 회원가입 오류 - username 중복")
    void error_saveUser_notUnique() throws Exception {
        //given
        UserForm userForm1 = UserForm.builder()
                .username("su")
                .password("123")
                .build();
        userService.save(userForm1);

        //when
        UserForm userForm2 = UserForm.builder()
                .username("su")
                .password("123123")
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.save(userForm2));
    }

    /*******************조회 *************************/
    @Test
    @DisplayName("사용자 조회")
    void findById() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("123")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        User findUser = userService.get(user.getId());

        //then
        Assertions.assertThat(findUser.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("사용자 조회 존재 x")
    void error_findById() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("123")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        assertThrows(UserNotFound.class, () -> userService.get(2L));
    }

    /*********************수정 (UPDATE) *****************/
    @Test
    @DisplayName("사용자 이름 변경")
    void updateUsername() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("123")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        UserEdit userEdit = UserEdit.builder()
                .username("park")
                .password("123")
                .build();
        userService.modify(user.getId(), userEdit);
        //then
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFound());

        assertThat(findUser.getUsername()).isEqualTo("park");
        assertThat(findUser).isEqualTo(user);
    }

    /***********************유저 삭제 (DELETE)***********************/
    @Test
    @DisplayName("유저 삭제")
    void deleteUser() throws Exception {
        //given
        User user = User.builder()
                .username("su")
                .password("123")
                .role(USER)
                .build();
        userRepository.save(user);

        //when
        userService.delete(user.getId());

        assertThat(userRepository.count()).isEqualTo(0);
    }
    @Test
    @DisplayName("에러_유저 삭제 존재 x")
    void error_deleteUser() throws Exception {

        //when
        assertThrows(UserNotFound.class, () -> userService.delete(1L));
    }

    @Test
    @DisplayName("인증권한 modify 테스트")
    @WithMockUser
    void authenticatedUser () throws Exception{
        User user = User.builder()
                .username("test")
                .role(USER)
                .build();

        assertThat(userService.save(user)).isNotNull();
    }

}