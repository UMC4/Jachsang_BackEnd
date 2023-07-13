package com.example.demo.src.post.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int postIdx;
    private int categoryIdx;
    private int userIdx;
    private String title;
    private int viewCount;
    private int likeCount;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String url;
}
