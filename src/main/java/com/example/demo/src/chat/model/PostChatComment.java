package com.example.demo.src.chat.model;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PostChatComment {
    private Long chatRoomIdx;
    private Long chatUserIdx;
    private String contents;
    private String kind;
    private int read;
    private Timestamp createTime;
    private String contentType;
}
