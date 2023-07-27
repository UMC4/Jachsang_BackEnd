package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommunityItemRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private String title;
    private String nickname;
    private int distance;
    private String createAt;
    private String imagePath;
}
