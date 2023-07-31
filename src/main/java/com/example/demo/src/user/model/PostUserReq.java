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
public class PostUserReq {
    private String userName;
    private String loginId;
    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private byte  adPolicyAgreement;
    private int status;
}
