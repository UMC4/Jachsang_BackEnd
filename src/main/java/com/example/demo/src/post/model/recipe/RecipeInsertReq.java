package com.example.demo.src.post.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeInsertReq {
    private String title;
    private String url; // 자취생 레시피 게시글 url
    private String thumbnailUrl;
    private String ingredients; // 응답 보낼 때 List<String>으로
    private String description; // 응답 보낼 때 List<String>으로
    private String mainImageUrl;
    private String originUrl; // 원 글 출처
}
