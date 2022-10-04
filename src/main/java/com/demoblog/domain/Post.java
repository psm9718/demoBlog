package com.demoblog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Table(name = "posts")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Lob //자바에서는 String 타입으로 가지고 있지만, 실제 DB에서는 long text 형식으로 저장되도록
    private String content;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    //User와 연관관계 편의 메서드
//    private void setUser(User user) {
//        this.user = user;
//        user.getPosts().add(this);
//    }

    //권장 x
    public void changeTitle(String title) {
        this.title = title;
    }

    /**
     * 호돌맨 패턴 (PostEditor 하나의 인자만 넘겨주도록 개선 가능, 수정해야 할 필드만 정의된 PostEditor 클래스 정의가능)
     * @return : PostEditorBuilder를 return
     */
    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
                .title(this.title)
                .title(this.content);
    }

    public void edit(PostEditor postEditor) {
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }

}
