package com.demoblog.config.auth.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(UserProfile userProfile) {
        this.name = userProfile.getName();
        this.email = userProfile.getEmail();
        this.picture = userProfile.getImageUrl();
    }

}
