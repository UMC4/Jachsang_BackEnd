package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userIdx;
    private String loginId;
    private String userName;
    private String nickname;
    private String password;
    private String email;
    private String phoneNumber;
    private int adPolicyAgreement;
    private int pushAgreement;
    private String lockPassword;
    private String bank;
    private String account;
    private double longitude;
    private double latitude;
    private int reported;
    private int groupPurchases;
    private String role;
    private int status;
}
