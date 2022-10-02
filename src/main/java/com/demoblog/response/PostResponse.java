package com.demoblog.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 서비스 정책에 맞는 클래스
 * - json 응답에서 title 값 길이를 최대 10글자로 해주세요.
 */
@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0,Math.min(title.length() , 10));
        //StringIndexOutOfBoundsException 방지를 위한 추가적인 처리
        this.content = content;
    }
}
