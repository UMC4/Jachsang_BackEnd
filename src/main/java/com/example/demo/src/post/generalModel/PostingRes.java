package com.example.demo.src.post.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostingRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private int userIdx;
    private String title;
    private String url;

}
