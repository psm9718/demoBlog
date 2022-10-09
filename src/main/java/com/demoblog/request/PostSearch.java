package com.demoblog.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * post 검색 필터, 정렬 방식 등에 따라 다양한 형태를 위한 별도 객체
 */
@Getter
@Setter
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    private Integer page;
    private Integer size;

    @Builder
    public PostSearch(Integer page, Integer size) {
        this.page = (page== null) ? 1 : page;
        this.size = (size== null) ? 10 : size;
    }

    public long getOffset() {
        return (long) Math.max(0, (page - 1)) * Math.min(size, MAX_SIZE);
    }
}
