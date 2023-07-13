package com.example.demo.src.post.groupPurchase;

import com.example.demo.src.post.generalModel.Post;
import com.example.demo.src.post.generalModel.PostingReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashMap;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchasePostingReq extends PostingReq {
    private int groupPurchaseDetailIdx;
    private int postIdx;
    private String productName;
    private String productURL;
    private double singlePrice;
    private double deliveryFee;
    private int members;
    private Timestamp deadline;
    private boolean hasExtension;
    private boolean calculated;

    public GroupPurchasePostingReq(HashMap<String,Object> req) {
        this.groupPurchaseDetailIdx = (int)req.get("postIdx");
        this.postIdx = this.groupPurchaseDetailIdx;
        this.productName = (String)req.get("productName");
        this.productURL = (String)req.get("productURL");
        this.singlePrice = (double)req.get("singlePrice");
        this.deliveryFee = (double)req.get("deliveryFee");
        this.deadline = (Timestamp)req.get("deadline");
        this.hasExtension = (boolean)req.get("hasExtension");
        this.calculated = (boolean)req.get("calculated");
    }
}
