package com.demoblog.request;

import com.demoblog.exception.DemoBlogException;
import com.demoblog.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostCreate {

    @NotBlank(message = "타이틀 값은 필수입니다.")
    private String title;

    @NotBlank(message = "컨텐츠 값은 필수입니다.")
    private String content;

    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest("title", "제목에 \'바보\'가 포함될 수 없습니다.");
        }
    }
}
