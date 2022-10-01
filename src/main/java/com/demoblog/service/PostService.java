package com.demoblog.service;

import com.demoblog.domain.Post;
import com.demoblog.domain.User;
import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = false)
    public String write(PostForm postForm) {

        Post post = Post.builder()
                .title(postForm.getTitle())
                .content(postForm.getContent()).build();

        postRepository.save(post);

        return post.getTitle();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}
