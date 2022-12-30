package com.demoblog.request;

import com.demoblog.domain.user.Role;
import com.demoblog.response.PasswordCheck;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * User 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserForm {

    @Size(min =  2, max = 20, message = "회원 아이디는 2글자 이상, 20글자 이하입니다.")
    @NotBlank(message = "회원 아이디는 필수 입니다. (2글자 이상, 20 글자 이하)")
    private String username;

    @PasswordCheck(message = "패스워드는 대,소문자 포함 8글자 이상입니다.")
    private String password;

//    @Email
//    private String email;


}
