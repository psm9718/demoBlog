package com.demoblog.repository;

import com.demoblog.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PostRepositoryTest {

    @Autowired PostRepository postRepository;
    
    @Test
    @DisplayName("글 생성 시 생성시간 등록 확인")
    void postCreateTime () throws Exception{
        //given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        //when
        postRepository.save(post);

        //then
        Assertions.assertThat(postRepository.findAll().get(0).getCreatedDate()).isNotNull();
    }

}