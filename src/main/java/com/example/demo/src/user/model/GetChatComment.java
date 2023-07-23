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
public class GetChatComment {

    private Long chatCommentIdx;
    private Long chatRoomIdx;
    private Long userIdx;
    private String contents;
    private String kind;
    private int unread;
    private Timestamp createTime;
    private String ContentType;
}
