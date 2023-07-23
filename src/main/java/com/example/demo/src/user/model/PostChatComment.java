package com.example.demo.src.user.model;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PostChatComment {
    private Long chatCommentIdx;
    private Long chatRoomIdx;
    private Long userIdx;
    private String contents;
    private String kind;
    private int read;
    private Timestamp createTime;
    private String contentType;
}
