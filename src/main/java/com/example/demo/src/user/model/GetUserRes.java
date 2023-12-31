package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
//
@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String userName;
    private String loginId;
    private String email;
    private String password;
    private String userImage;
    private String nickname;
}
