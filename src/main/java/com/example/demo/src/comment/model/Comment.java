package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int commentIdx;
    private int postIdx;
    private int parentCommentIdx;
    private int userIdx;
    private int likeCount;
    private String contents;
    private Timestamp createAt;
    private Timestamp updateAt;
}
