package com.demoblog.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * 수정가능한 필드에 대해 별도 정의
 */
@Getter
public class PostEditor {

    private final String title;
    private final String content;

    @Builder
    public PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
