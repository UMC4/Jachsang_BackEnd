package com.example.demo.src.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommunityActivityRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private String title;
    private String createAt;
    private int commentCount;
}
