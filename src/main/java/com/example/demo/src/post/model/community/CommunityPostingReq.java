package com.example.demo.src.post.model.community;

import com.example.demo.src.post.model.generalModel.PostingReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostingReq extends PostingReq {
    private int communityDetailIdx;
    private String contents;
}
