package com.demoblog.service;

import com.demoblog.domain.User;
import com.demoblog.repository.UserRepository;
import com.demoblog.request.UserForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("사용자 회원가입")
    void saveUser () throws Exception{
        //given
        UserForm userForm = UserForm.builder()
                .username("su")
                .password("123")
                        .build();

        //when
        userService.save(userForm);

        //then
        User user = userRepository.findAll().get(0);
        assertThat(userRepository.count()).isSameAs(1);
        assertThat(user.getUsername()).isEqualTo("su");
    }

    @Test
    @DisplayName("사용자 조회")
    void findById () throws Exception{
        //given
        User user = User.builder()
                .username("su")
                .password("123")
                .build();
        userRepository.save(user);

        //when
        User findUser = userService.get(user.getId());

        //then
        Assertions.assertThat(findUser.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
    }


}