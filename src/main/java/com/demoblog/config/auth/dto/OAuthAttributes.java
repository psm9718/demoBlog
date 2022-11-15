package com.demoblog.config.auth.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        return new UserProfile(
                String.valueOf(attributes.get("sub")),
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("picture")
        );
    }),
    KAKAO("kakao", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>)response.get("profile");
        return new UserProfile(
                String.valueOf(attributes.get("id")),
                (String) kakaoProfile.get("nickname"),
                (String) response.get("email"),
                (String) kakaoProfile.get("profile_image_url")
        );
    });
//
//    GITHUB("github", (attributes) -> {
//        return new UserProfile(
//                String.valueOf(attributes.get("id")),
//                (String) attributes.get("name"),
//                (String) attributes.get("email"),
//                (String) attributes.get("avatar_url")
//        );
//    }),
//    NAVER("naver", (attributes) -> {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//        return new UserProfile(
//                (String) response.get("id"),
//                (String) response.get("name"),
//                (String) response.get("email"),
//                (String) response.get("profile_image")
//        );
//    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}