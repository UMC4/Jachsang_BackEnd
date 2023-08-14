package com.example.demo.src.board;

import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

import static com.example.demo.src.board.model.GetCommunityItemRes.communityRowMapper;
import static com.example.demo.src.board.model.GetGroupPurchaseItemRes.groupPurchaseRowMapper;
import static com.example.demo.src.board.model.GetRecipeItemRes.recipeRowMapper;

@Repository
public class BoardDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    // 특정 카테고리의 커뮤니티 게시물을 가져오는 메서드입니다.
    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 생성 시간을 기준으로 내림차순으로 정렬됩니다.
    public List<GetCommunityItemRes> filterCommunityByCategory(int userIdx, int categoryIdx, int startIdx, int size) {
        String Query =
                "WITH cte AS (" +
                    "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, imagePath, " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                    "FROM Post P " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                        "JOIN User Author ON P.userIdx = Author.userIdx " +
                        "LEFT JOIN ( " +
                            "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                            "FROM Image  " +
                            "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                    "WHERE P.categoryIdx = ?" +
                ") " +
                "SELECT postIdx, categoryIdx, category, title, nickname, distance, createAt, imagePath " +
                "FROM cte " +
                "WHERE distance IS NULL OR distance <= 3000 " +
                "ORDER BY createAt DESC " +
                "LIMIT ? OFFSET ?";


        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, categoryIdx, size, startIdx);
    }

    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 생성 시간을 기준으로 내림차순으로 정렬됩니다.
    // 공지는 먼저 정렬되고 그다음에 일반 게시물이 정렬됩니다.
    public List<GetCommunityItemRes> sortCommunityByLatest(int userIdx, int startIdx, int size) {
        String Query =
                "WITH cte AS (" +
                    "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, imagePath, " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                    "FROM Post P " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                        "JOIN User Author ON P.userIdx = Author.userIdx " +
                        "LEFT JOIN ( " +
                            "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                            "FROM Image  " +
                            "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                    "WHERE FLOOR(P.categoryIdx/10) = 1" +
                ") " +
                "SELECT postIdx, categoryIdx, category, title, nickname, distance, createAt, imagePath " +
                "FROM cte " +
                "WHERE distance IS NULL OR distance <= 3000 " +
                "ORDER BY IF(categoryIdx = 15, 0, 1), createAt DESC " +
                "LIMIT ? OFFSET ?";


        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }


    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 인기를 기준으로 내림차순으로 정렬됩니다.
    // 공지는 먼저 정렬되며, 그다음에 일주일 이내에 생성된 게시물이 정렬되고, 마지막으로 일주일 이후에 생성된 게시물이 정렬됩니다.
    // 공지는 최신순, 일반 게시물은 인기순으로 정렬됩니다.
    // 인기도 = (100*조회수 + 공감수)
    public List<GetCommunityItemRes> sortCommunityByPopularity(int userIdx, int startIdx, int size) {
        String Query =
                    "WITH cte AS ( " +
                        "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, " +
                            "P.createAt, imagePath, (100*P.likeCount + P.viewCount) AS weight, " +
                            "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance, " +
                            "IF(P.createAt >= TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), 1, 0) AS isNew " +
                        "FROM Post P " +
                            "JOIN PostCategory AS PC ON P.categoryIdx = PC.categoryIdx " +
                            "JOIN User AS Author ON P.userIdx = Author.userIdx " +
                            "LEFT JOIN ( " +
                                "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                                "FROM Image  " +
                                "GROUP BY postIdx  " +
                            ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                            "JOIN User AS U ON U.userIdx = ? " +
                        "WHERE FLOOR(P.categoryIdx/10) = 1" +
                    ") " +
                    "SELECT postIdx, categoryIdx, category, title, nickname, distance, createAt, imagePath " +
                    "FROM cte " +
                    "WHERE distance IS NULL OR distance <= 3000 " +
                    "ORDER BY " +
                        "IF(categoryIdx = 15, 0, 1)," +
                        "IF(categoryIdx = 15, createAt, NULL) DESC," +
                        "IF(isNew = 1, weight, NULL) DESC," +
                        "IF(isNew = 0, weight, NULL) DESC " +
                    "LIMIT ? OFFSET ?";


        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, size, startIdx);
    }

    public List<GetCommunityItemRes> searchCommunity(int userIdx, String query, int startIdx, int size) {
        String Query =
                "WITH cte AS ( " +
                        "SELECT P.postIdx, PC.categoryIdx, PC.category, P.title, Author.nickname, P.createAt, imagePath, " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                        "FROM Post P " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx " +
                        "JOIN User Author ON P.userIdx = Author.userIdx  " +
                        "LEFT JOIN (SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath FROM Image GROUP BY postIdx) MinIdImage " +
                        "ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                        "WHERE FLOOR(P.categoryIdx/10) = 1 " +
                        ") " +
                        "SELECT postIdx, categoryIdx, category, title, nickname, distance, createAt, imagePath " +
                        "FROM cte " +
                        "WHERE (distance IS NULL OR distance <= 3000) " +
                        "AND (MATCH(title) AGAINST(? IN NATURAL LANGUAGE MODE)) " +
                        "LIMIT ? OFFSET ?";


        return this.jdbcTemplate.query(Query, communityRowMapper, userIdx, query, size, startIdx);
    }

    // 특정 카테고리의 공동구매 게시물을 가져오는 메서드입니다.
    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 생성 시간을 기준으로 내림차순으로 정렬됩니다.
    public List<GetGroupPurchaseItemRes> filterGroupPurchaseByCategory(int userIdx, int categoryIdx, int startIdx, int size) {
        String Query =
                "WITH cte AS ( " +
                    "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, " +
                        "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay, imagePath,  " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                    "FROM Post P  " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                        "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                        "JOIN User Author ON P.userIdx = Author.userIdx  " +
                        "LEFT JOIN ( " +
                            "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                            "FROM Image  " +
                            "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                    "WHERE P.categoryIdx = ? " +
                ") " +
                    "SELECT postIdx, categoryIdx, category, title, productName, nickname, distance, createAt, remainDay, imagePath  " +
                    "FROM cte  " +
                    "WHERE distance IS NULL OR distance <= 3000  " +
                    "ORDER BY createAt DESC " +
                    "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, categoryIdx, size, startIdx);
    }

    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 생성 시간을 기준으로 내림차순으로 정렬됩니다.
    public List<GetGroupPurchaseItemRes> sortGroupPurchaseByLatest(int userIdx, int startIdx, int size) {
        String Query =
                "WITH cte AS ( " +
                    "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay, imagePath,  " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                    "FROM Post P  " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                        "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                        "JOIN User Author ON P.userIdx = Author.userIdx  " +
                        "LEFT JOIN ( " +
                            "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                            "FROM Image  " +
                            "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                    "WHERE FLOOR(P.categoryIdx/10) = 2" +
                ") " +
                    "SELECT postIdx, categoryIdx, category, title, productName, nickname, distance, createAt, remainDay, imagePath  " +
                    "FROM cte  " +
                    "WHERE distance IS NULL OR distance <= 3000  " +
                    "ORDER BY createAt DESC " +
                    "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, size, startIdx);
    }

    // 3000미터 이내의 거리에 있는 게시물만 반환하며, 게시물 마감기한까지 기간이 짧은 순서(마감임박순)로 정렬합니다.
    public List<GetGroupPurchaseItemRes> sortGroupPurchaseByRemainTime(int userIdx, int startIdx, int size) {
        String Query =
                "WITH cte AS ( " +
                    "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, GPD.deadline, imagePath, " +
                        "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay,  " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                    "FROM Post P  " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                        "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                        "JOIN User Author ON P.userIdx = Author.userIdx  " +
                        "LEFT JOIN ( " +
                            "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                            "FROM Image  " +
                            "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                    "JOIN User U ON U.userIdx = ? " +
                    "WHERE FLOOR(P.categoryIdx/10) = 2" +
                ") " +
                "SELECT postIdx, categoryIdx, category, title, productName, nickname, distance, createAt, deadline, remainDay, imagePath  " +
                "FROM cte  " +
                "WHERE distance IS NULL OR distance <= 3000  " +
                "ORDER BY (remainDay < 0), " +
                        "CASE WHEN remainDay >= 0 THEN deadline END ASC, " +
                        "CASE WHEN remainDay < 0 THEN deadline END DESC " +
                "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, size, startIdx);
    }

    public List<GetGroupPurchaseItemRes> searchGroupPurchase(int userIdx, String query, int startIdx, int size) {
        String Query =
                "WITH cte AS (" +
                        "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, Author.nickname, P.createAt, TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay, imagePath, " +
                        "IF(U.role = 1 OR Author.role = 1, NULL, ST_DISTANCE_SPHERE(POINT(Author.longitude, Author.latitude), POINT(U.longitude, U.latitude))) AS distance " +
                        "FROM Post P  " +
                        "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                        "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                        "JOIN User Author ON P.userIdx = Author.userIdx  " +
                        "LEFT JOIN (" +
                        "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                        "FROM Image  " +
                        "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                        "JOIN User U ON U.userIdx = ? " +
                        "WHERE FLOOR(P.categoryIdx/10) = 2" +
                        ") " +
                        "SELECT postIdx, categoryIdx, category, title, productName, nickname, distance, createAt, remainDay, imagePath  " +
                        "FROM cte  " +
                        "WHERE (distance IS NULL OR distance <= 3000) " +
                        "AND MATCH(title) AGAINST(? IN NATURAL LANGUAGE MODE) " +
                        "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, query, size, startIdx);
    }

    // 모든 레시피 게시물을 가져오는 메서드입니다.
    // 게시물 생성 시간을 기준으로 내림차순으로 정렬됩니다.
    public List<GetRecipeItemRes> sortRecipeByLatest(int userIdx, int startIdx, int size) {
        String Query =
                "SELECT P.postIdx, P.title, P.likeCount, imagePath, " +
                        "IF(LP.postIdx IS NOT NULL, TRUE, FALSE) AS likestatus " +
                "FROM Post P " +
                    "LEFT JOIN LikedPost LP ON P.postIdx = LP.postIdx AND LP.userIdx = ? " +
                    "LEFT JOIN ( " +
                        "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                        "FROM Image  " +
                        "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                "WHERE FLOOR(P.categoryIdx / 10) = 3 " +
                "ORDER BY P.createAt DESC " +
                "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, recipeRowMapper, userIdx, size, startIdx);
    }

    // 모든 레시피 게시물을 가져오는 메서드입니다.
    // 먼저 일주일 이내에 생성된 게시물이 정렬되고, 마지막으로 일주일 이후에 생성된 게시물이 정렬됩니다.
    // 인기순으로 정렬됩니다.
    // 인기도 = (100*조회수 + 공감수)
    public List<GetRecipeItemRes> sortRecipeByPopularity(int userIdx, int startIdx, int size) {
        String Query =
                "SELECT P.postIdx, P.title, P.likeCount, imagePath, " +
                    "IF(LP.postIdx IS NOT NULL, TRUE, FALSE) AS likestatus, P.likeCount " +
                "FROM Post P " +
                    "LEFT JOIN LikedPost LP ON P.postIdx = LP.postIdx AND LP.userIdx = ? " +
                    "LEFT JOIN ( " +
                        "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                        "FROM Image  " +
                        "GROUP BY postIdx  " +
                    ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                "WHERE FLOOR(P.categoryIdx / 10) = 3 " +
                "ORDER BY IF(P.createAt >= TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), 1, 0)," +
                    "(100*P.likeCount+P.viewCount) DESC " +
                "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, recipeRowMapper, userIdx, size, startIdx);
    }

    public List<GetRecipeItemRes> searchRecipe(int userIdx, String query, boolean isTagSearch, int startIdx, int size) {
        String Query =
                "SELECT P.postIdx, P.title, P.likeCount, imagePath, IF(LP.postIdx IS NOT NULL, TRUE, FALSE) AS likestatus " +
                "FROM Post P " +
                    "JOIN RecipeDetail RD ON P.postIdx = RD.postIdx " +
                    "LEFT JOIN LikedPost LP ON P.postIdx = LP.postIdx AND LP.userIdx = ? " +
                    "LEFT JOIN ( " +
                        "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                        "FROM Image  " +
                        "GROUP BY postIdx  " +
                    ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                "WHERE FLOOR(P.categoryIdx/10) = 3 AND " +
                (isTagSearch ? "MATCH(RD.ingredients) AGAINST(? IN NATURAL LANGUAGE MODE) " : "MATCH(title) AGAINST(? IN NATURAL LANGUAGE MODE) ") +
                "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, recipeRowMapper, userIdx, query, size, startIdx);
    }
}