package com.demoblog.config.auth.dto;

import com.demoblog.domain.user.Role;
import com.demoblog.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserProfile {

    private final String oauthId;
    private final String name;
    private final String email;
    private final String imageUrl;


    public User toUser() {
        return User.builder()
                .oauthId(oauthId)
                .username(name)
                .email(email)
                .imageUrl(imageUrl)
                .role(Role.USER)
                .build();
    }

}
