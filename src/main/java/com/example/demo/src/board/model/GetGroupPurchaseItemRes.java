package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetGroupPurchaseItemRes {
    private int postIdx;
    private String category;
    private String title;
    private String productName;
    private String nickname;
    private int distance;
    private int remainDay;
    private String imagePath;
}
