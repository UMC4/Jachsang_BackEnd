package com.example.demo.src.user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserChatRes {
    private int chatUserIdx;
    private int chatRoomIdx;
    private int userIdx;
}
