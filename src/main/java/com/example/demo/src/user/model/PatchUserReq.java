package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserReq {
    private int userIdx;
    private String nickname;
    private String phoneNumber;
    private String password;
    private String email;

}
