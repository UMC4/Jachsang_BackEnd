package com.example.demo.src.mypage;

import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import com.example.demo.src.mypage.model.GetGroupPurchaseActivityRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MypageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    public List<GetCommunityActivityRes> getMyCommunityPosts(int userIdx) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount " +
                "FROM Post P " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                    "JOIN User Author ON P.userIdx = Author.userIdx " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 1 AND P.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetCommunityActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount")),
                userIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityComments(int userIdx) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount " +
                "FROM Post P " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                    "JOIN User Author ON P.userIdx = Author.userIdx " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 1 AND C.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetCommunityActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount")),
                userIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityLikes(int userIdx) {
        String Query =
                "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount " +
                "FROM Post P " +
                    "JOIN LikedPost LP on LP.postIdx = P.postIdx " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                    "JOIN User Author ON P.userIdx = Author.userIdx " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 1 AND LP.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetCommunityActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount")),
                userIdx);
    }

    public List<GetCommunityActivityRes> getMyCommunityHearts(int userIdx) {
        String Query =
                "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount " +
                "FROM Post P " +
                    "JOIN HeartPost HP on HP.postIdx = P.postIdx " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                    "JOIN User Author ON P.userIdx = Author.userIdx " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 1 AND HP.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetCommunityActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount")),
                userIdx);
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount, " +
                    "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay " +
                "FROM Post P  " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                    "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                    "JOIN User Author ON P.userIdx = Author.userIdx  " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 2 AND P.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetGroupPurchaseActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("productName"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount"),
                        rs.getInt("remainDay")),
                userIdx);
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount, " +
                    "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay " +
                "FROM Post P  " +
                    "JOIN LikedPost LP on LP.postIdx = P.postIdx " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                    "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                    "JOIN User Author ON P.userIdx = Author.userIdx  " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 2 AND LP.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetGroupPurchaseActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("productName"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount"),
                        rs.getInt("remainDay")),
                userIdx);
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, " +
                    "COUNT(C.commentIdx) as commentCount, " +
                    "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay " +
                "FROM Post P  " +
                    "JOIN ChatRoom CR on P.postIdx = CR.postIdx " +
                    "JOIN ChatUser CU on CR.chatRoomIdx = CU.chatRoomIdx " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                    "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                    "JOIN User Author ON P.userIdx = Author.userIdx  " +
                    "LEFT JOIN Comment C ON P.postIdx = C.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 2 AND CU.userIdx = ? " +
                "GROUP BY P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt " +
                "ORDER BY P.createAt";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new GetGroupPurchaseActivityRes(
                        rs.getInt("postIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("productName"),
                        rs.getString("createAt"),
                        rs.getInt("commentCount"),
                        rs.getInt("remainDay")),
                userIdx);
    }
}