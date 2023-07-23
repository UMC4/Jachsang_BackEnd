package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPost {
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


