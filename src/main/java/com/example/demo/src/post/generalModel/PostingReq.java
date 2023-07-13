package com.example.demo.src.post.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostingReq{
    private String boardName;
    private String category;
    private int userIdx;
    private String title;

    public PostingReq(int userIdx, String title){
        this.userIdx = userIdx;
        this.title = title;
    }
}
