package com.demoblog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    private static final int MIN_USERID_LENGTH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @Size(min = 2, max = 20)
    @NotBlank(message = "회원 아이디는 필수 입니다.")
    private String username;


    @Column(name = "password")
    @NotBlank(message = "회원 비밀번호는 필수 입니다.")
    private String password;

//    @OneToMany(cascade = CascadeType.ALL)
//    private List<Post> posts = new ArrayList<>();
}
