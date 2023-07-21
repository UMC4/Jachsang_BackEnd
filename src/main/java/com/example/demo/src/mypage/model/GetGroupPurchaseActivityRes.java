package com.example.demo.src.mypage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGroupPurchaseActivityRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private String title;
    private String productName;
    private String createAt;
    private int commentCount;
    private int remainDay;
}
