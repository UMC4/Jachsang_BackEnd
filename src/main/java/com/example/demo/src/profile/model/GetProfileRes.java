package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProfileRes {
    private String nickname;
    private double longitude;
    private double latitude;
    private int distance;
    private int communityPostCount;
    private int commentCount;
    private int reportCount;
}
