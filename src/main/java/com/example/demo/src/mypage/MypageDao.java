package com.example.demo.src.mypage;

import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import com.example.demo.src.mypage.model.GetGroupPurchaseActivityRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MypageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    private String communityQuery(String additionalJoin, String condition) {
        return "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, P.createAt, " +
                "COUNT(C.commentIdx) as commentCount " +
                "FROM Post P " +
                additionalJoin +
                "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 1 AND " + condition + ".userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, P.createAt " +
                "ORDER BY P.createAt " +
                "LIMIT ? OFFSET ?";
    }

    private final RowMapper<GetCommunityActivityRes> communityRowMapper =
            (rs,rowNum) -> new GetCommunityActivityRes(
                    rs.getInt("postIdx"),
                    rs.getInt("categoryIdx"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("createAt"),
                    rs.getInt("commentCount")
            );

    public List<GetCommunityActivityRes> getMyCommunityPosts(int userIdx, int startIdx, int size) {
        String Query = communityQuery("", "P");
        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityComments(int userIdx, int startIdx, int size) {
        String Query = communityQuery("", "C");
        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityLikes(int userIdx, int startIdx, int size) {
        String Query = communityQuery("JOIN LikedPost LP on LP.postIdx = P.postIdx ", "LP");
        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityHearts(int userIdx, int startIdx, int size) {
        String Query = communityQuery("JOIN LikedPost HP on HP.postIdx = P.postIdx ", "HP");
        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }

    private String groupPurchaseQuery(String additionalJoin, String condition) {
        return "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, " +
                "COUNT(C.commentIdx) as commentCount, " +
                "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay " +
                "FROM Post P  " +
                additionalJoin +
                "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                "JOIN User Author ON P.userIdx = Author.userIdx  " +
                "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 2 AND " + condition + ".userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt " +
                "ORDER BY P.createAt " +
                "LIMIT ? OFFSET ?";
    }

    private final RowMapper<GetGroupPurchaseActivityRes> groupPurchaseRowMapper =
            (rs,rowNum) -> new GetGroupPurchaseActivityRes(
                    rs.getInt("postIdx"),
                    rs.getInt("categoryIdx"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("productName"),
                    rs.getString("createAt"),
                    rs.getInt("commentCount"),
                    rs.getInt("remainDay")
            );

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx, int startIdx, int size) {
        String Query = groupPurchaseQuery("", "P");
        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, size, startIdx);
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx, int startIdx, int size) {
        String Query = groupPurchaseQuery("JOIN LikedPost LP on LP.postIdx = P.postIdx ", "LP");
        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, size, startIdx);
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx, int startIdx, int size) {
        String Query = groupPurchaseQuery(
                "JOIN ChatRoom CR on P.postIdx = CR.postIdx " +
                        "JOIN ChatUser CU on CR.chatRoomIdx = CU.chatRoomIdx ",
                "CU"
        );
        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, size, startIdx);
    }
}