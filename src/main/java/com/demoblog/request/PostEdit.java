package com.demoblog.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostEdit {

    @NotBlank(message = "타이틀 값은 필수입니다.")
    private String title;

    @NotBlank(message = "컨텐츠 값은 필수입니다.")
    private String content;

}
