package com.example.demo.src.post.model.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {
    private int communityDetailIdx;
    private int heartCount;
    private String contents;
}
