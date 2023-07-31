package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRooms {
    private Long chatRoomIdx;
    private Long userIdx;
    private String title;
    private int unreads;
    private Timestamp updateTime;
    private String contents;
}
