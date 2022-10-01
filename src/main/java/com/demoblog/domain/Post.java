package com.demoblog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@Table(name = "posts")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Lob //자바에서는 String 타입으로 가지고 있지만, 실제 DB에서는 long text 형식으로 저장되도록
    private String content;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    //User와 연관관계 편의 메서드
//    private void setUser(User user) {
//        this.user = user;
//        user.getPosts().add(this);
//    }
}
