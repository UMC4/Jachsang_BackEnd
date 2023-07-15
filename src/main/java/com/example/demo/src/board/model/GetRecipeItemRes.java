package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRecipeItemRes {
    private int postIdx;
    private String title;
    private String tag;
    private boolean likeStatus;
    private int likeCount;
    private String imagePath;
}
