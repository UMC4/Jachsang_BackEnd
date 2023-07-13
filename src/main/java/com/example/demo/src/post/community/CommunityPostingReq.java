package com.example.demo.src.post.community;

import com.example.demo.src.post.generalModel.Post;
import com.example.demo.src.post.generalModel.PostingReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostingReq extends PostingReq {
    private int communityDetailIdx;
    private String contents;
}
