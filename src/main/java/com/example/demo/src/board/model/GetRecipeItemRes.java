package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

@Getter
@Setter
@AllArgsConstructor
public class GetRecipeItemRes {
    private int postIdx;
    private String title;
    private boolean likeStatus;
    private int likeCount;
    private String mainImageUrl;

    public static RowMapper<GetRecipeItemRes> recipeRowMapper =
            (rs,rowNum) -> new GetRecipeItemRes(
                    rs.getInt("postIdx"),
                    rs.getString("title"),
                    rs.getBoolean("likeStatus"),
                    rs.getInt("likeCount"),
                    rs.getString("mainImageUrl")
            );
}
