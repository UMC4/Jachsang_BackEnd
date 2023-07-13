package com.example.demo.src.post.community;

import com.example.demo.src.post.generalModel.GetGeneralPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCommunityPostRes {
    private int postIdx;
    private int categoryIdx;
    private int userIdx;
    private String title;
    private int viewCount;
    private int likeCount;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String url;
    private int communityDetailIdx;
    private String contents;

    public GetCommunityPostRes(GetGeneralPost generalPost, CommunityPost communityPost) {
        this.postIdx = generalPost.getPostIdx();
        this.categoryIdx = generalPost.getCategoryIdx();
        this.userIdx = generalPost.getUserIdx();
        this.title = generalPost.getTitle();
        this.viewCount = generalPost.getViewCount();
        this.likeCount = generalPost.getLikeCount();
        this.createAt = generalPost.getCreateAt();
        this.updateAt = generalPost.getUpdateAt();
        this.url = generalPost.getUrl();
        this.communityDetailIdx = communityPost.getCommunityDetailIdx();
        this.contents = communityPost.getContents();
    }
}
