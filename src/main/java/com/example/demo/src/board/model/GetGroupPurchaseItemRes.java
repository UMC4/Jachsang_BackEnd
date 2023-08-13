package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

@Getter
@Setter
@AllArgsConstructor
public class GetGroupPurchaseItemRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private String title;
    private String productName;
    private String nickname;
    private int distance;
    private int remainDay;
    private String imagePath;

    public static RowMapper<GetGroupPurchaseItemRes> groupPurchaseRowMapper =
            (rs,rowNum) -> new GetGroupPurchaseItemRes(
                    rs.getInt("postIdx"),
                    rs.getInt("categoryIdx"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("productName"),
                    rs.getString("nickname"),
                    rs.getInt("distance"),
                    rs.getInt("remainDay"),
                    rs.getString("imagePath")
            );
}
