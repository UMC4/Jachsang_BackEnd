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
public class GetChatUser {
    private Long chatUserIdx;
    private Long userIdx;
    private Timestamp createTime;
    private Long chatRoomIdx;
}

