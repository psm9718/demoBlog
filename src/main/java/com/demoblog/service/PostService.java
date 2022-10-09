package com.demoblog.service;

import com.demoblog.domain.Post;
import com.demoblog.domain.PostEditor;
import com.demoblog.exception.PostNotFound;
import com.demoblog.repository.PostRepository;
import com.demoblog.request.PostEdit;
import com.demoblog.request.PostForm;
import com.demoblog.request.PostSearch;
import com.demoblog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        /**
         * 응답 클래스 분리
         */

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

//    public List<PostResponse> getList(Pageable pageable) {
//        return postRepository.findAll(pageable).stream()
//                .map(post -> new PostResponse(post))
//                .collect(Collectors.toList());
//
//    }

    /**
     * querydsl 사용
     * @param postSearch
     * @return
     */
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(post -> new PostResponse(post))
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();
        //아직 fix가 되지 않았기 때문에 변경이 필요한 데이터 수정해서 넘겨줌.
        PostEditor postEditor = editorBuilder.title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);


    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        postRepository.delete(post);
    }
}
