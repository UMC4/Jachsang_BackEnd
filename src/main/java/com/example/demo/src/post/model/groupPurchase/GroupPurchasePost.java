package com.example.demo.src.post.model.groupPurchase;

import com.example.demo.src.post.model.generalModel.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchasePost extends Post {
    private int groupPurchaseDetailIdx;
    private String productName;
    private String productURL;
    private double singlePrice;
    private double deliveryFee;
    private int members;
    private Timestamp deadline;
    private boolean hasExtension;
    private boolean calculated;
}
