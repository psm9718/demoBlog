package com.demoblog.repository;

import com.demoblog.domain.Post;
import com.demoblog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
