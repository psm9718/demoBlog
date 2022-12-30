package com.demoblog.service;


import com.demoblog.config.auth.dto.UserProfile;
import com.demoblog.domain.user.Role;
import com.demoblog.domain.user.User;
import com.demoblog.exception.UserNotFound;
import com.demoblog.repository.UserRepository;
import com.demoblog.request.UserEdit;
import com.demoblog.request.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.demoblog.domain.user.Role.USER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long save(UserForm userForm) {

        User user = User.builder()
                .username(userForm.getUsername())
                .password(userForm.getPassword())
                .role(USER)
                .build();

        userRepository.save(user);

        return user.getId();
    }

    public User get(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound());
    }

    @Transactional
//    @PreAuthorize("isAuthenticated()")
    public void modify(Long id, UserEdit userEdit) {
        log.info("User {} : Modify", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound());

        user.update(userEdit);
    }

    public void delete(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFound());
        userRepository.delete(findUser);
    }

    @PreAuthorize("isAuthenticated()")
    public Long save(User user) {
        return userRepository.save(user).getId();
    }
}
