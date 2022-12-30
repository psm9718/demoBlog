package com.demoblog.domain.user;

import com.demoblog.config.auth.dto.UserProfile;
import com.demoblog.domain.BaseTimeEntity;
import com.demoblog.domain.Post;
import com.demoblog.request.UserEdit;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "unique_username", columnNames = {"username"})})
public class User extends BaseTimeEntity {

    private static final int MIN_USERID_LENGTH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String oauthId;


    @Column(name = "username")
    @Size(min = 2, max = 20)
    @NotBlank(message = "회원 아이디는 필수 입니다.")
    private String username;


    @Column(name = "password")
//    @NotBlank(message = "회원 비밀번호는 필수 입니다.")
    private String password;

    private String email;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void update(UserEdit userEdit) {
        username = userEdit.getUsername();
        password = userEdit.getPassword();
    }

    public User update(UserProfile userProfile) {

        this.username = userProfile.getName();
        this.email = userProfile.getEmail();
        this.imageUrl = userProfile.getImageUrl();
        return this;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private final List<Post> posts = new ArrayList<>();

    public String getRoleKey() {
        return this.role.getKey();
    }
}
