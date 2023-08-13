package com.example.demo.src.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

@Getter
@Setter
@AllArgsConstructor
public class GetCommunityItemRes {
    private int postIdx;
    private int categoryIdx;
    private String category;
    private String title;
    private String nickname;
    private int distance;
    private String createAt;
    private String imagePath;

    public static RowMapper<GetCommunityItemRes> communityRowMapper =
            (rs,rowNum) -> new GetCommunityItemRes(
                    rs.getInt("postIdx"),
                    rs.getInt("categoryIdx"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("nickname"),
                    rs.getInt("distance"),
                    rs.getString("createAt"),
                    rs.getString("imagePath")
            );
}

