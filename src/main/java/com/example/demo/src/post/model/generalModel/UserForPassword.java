package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserForPassword {
    private int userIdx;
    private String loginId;
    private String userName;
    private String password;
    private String email;
}
