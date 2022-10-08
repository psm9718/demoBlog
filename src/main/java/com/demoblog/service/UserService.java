package com.demoblog.service;


import com.demoblog.domain.User;
import com.demoblog.exception.UserNotFound;
import com.demoblog.repository.UserRepository;
import com.demoblog.request.UserEdit;
import com.demoblog.request.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .build();

        userRepository.save(user);

        return user.getId();
    }

    public User get(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound());
    }

    @Transactional
    public void modify(Long id, UserEdit userEdit) {
        log.info("User Modify");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound());

        user.update(userEdit);
    }

    public void delete(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFound());
        userRepository.delete(findUser);
    }
}
