package com.example.demo.src.post.model.groupPurchase;

import com.example.demo.src.post.model.generalModel.Image;
import com.example.demo.src.post.model.generalModel.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupPurchasePostRes extends Image {
    private int postIdx;
    private int categoryIdx;
    private int userIdx;
    private String title;
    private int viewCount;
    private int likeCount;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String url;
    private int groupPurchaseDetailIdx;
    private String productName;
    private String productURL;
    private double singlePrice;
    private double deliveryFee;
    private int members;
    private Timestamp deadline;
    private boolean hasExtension;
    private boolean calculated;
    private List<Integer> comments;
    private boolean isLiked;
    private boolean isScraped;
    public GetGroupPurchasePostRes(Post generalPost, GroupPurchasePost groupPurchasePost, List<String> paths){
        super(paths);
        this.postIdx = generalPost.getPostIdx();
        this.categoryIdx = generalPost.getCategoryIdx();
        this.userIdx = generalPost.getUserIdx();
        this.title = generalPost.getTitle();
        this.viewCount = generalPost.getViewCount();
        this.likeCount = generalPost.getLikeCount();
        this.createAt = generalPost.getCreateAt();
        this.updateAt = generalPost.getUpdateAt();
        this.url = generalPost.getUrl();
        this.groupPurchaseDetailIdx = groupPurchasePost.getGroupPurchaseDetailIdx();
        this.productName = groupPurchasePost.getProductName();
        this.productURL = groupPurchasePost.getProductURL();
        this.singlePrice = groupPurchasePost.getSinglePrice();
        this.deliveryFee = groupPurchasePost.getDeliveryFee();
        this.members = groupPurchasePost.getMembers();
        this.deadline = groupPurchasePost.getDeadline();
        this.hasExtension = groupPurchasePost.isHasExtension();
        this.calculated = groupPurchasePost.isCalculated();
    }
}
