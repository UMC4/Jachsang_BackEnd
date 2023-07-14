package com.example.demo.src.post.model.groupPurchase;

import com.example.demo.src.post.model.generalModel.PostingReq;
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

    public GroupPurchasePostingReq(int postIdx, HashMap<String,Object> req) {
        this.groupPurchaseDetailIdx = postIdx;
        this.postIdx = this.groupPurchaseDetailIdx;
        this.productName = (String)req.get("productName");
        this.productURL = (String)req.get("productURL");
        this.singlePrice = (int)req.get("singlePrice");
        this.deliveryFee = (int)req.get("deliveryFee");
        this.members = (int)req.get("members");
        this.deadline = Timestamp.valueOf((String)req.get("deadline"));
        this.hasExtension = false;
        this.calculated = false;
    }
}
