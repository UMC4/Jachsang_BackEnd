package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.profile.model.GetProfileRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

import static com.example.demo.src.board.model.GetGroupPurchaseItemRes.groupPurchaseRowMapper;

@Repository
public class ProfileDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetProfileRes getProfile(int profileUserIdx, int userIdx) throws BaseException {
        String Query =
                "SELECT profileU.nickname, profileU.longitude, profileU.latitude, " +
                    "ST_DISTANCE_SPHERE(POINT(profileU.longitude, profileU.latitude), POINT(U.longitude, U.latitude)) AS distance, " +
                    "COUNT(DISTINCT IF(FLOOR(P.categoryIdx/10) = 1, P.postIdx, NULL)) AS communityPostCount, " +
                    "COUNT(DISTINCT C.commentIdx) AS commentCount, " +
                    "COUNT(DISTINCT R.reportIdx) AS reportedCount " +
                "FROM User profileU " +
                    "JOIN User as U ON U.userIdx = ? " +
                    "LEFT JOIN Post P ON profileU.userIdx = P.userIdx " +
                    "LEFT JOIN Comment C ON profileU.userIdx = C.userIdx " +
                    "LEFT JOIN Report R ON profileU.userIdx = R.reportedUserIdx " +
                "WHERE profileU.userIdx = ?";

        try {
            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rowNum) -> new GetProfileRes(
                            rs.getString("nickname"),
                            rs.getDouble("longitude"),
                            rs.getDouble("latitude"),
                            rs.getInt("distance"),
                            rs.getInt("communityPostCount"),
                            rs.getInt("commentCount"),
                            rs.getInt("reportedCount")),
                    userIdx, profileUserIdx);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USER);
        }
    }

    public List<GetGroupPurchaseItemRes> getGroupPurchaseList(int userIdx, int profileUserIdx, int startIdx, int size) {
        String Query =
                "SELECT P.postIdx, P.categoryIdx, PC.category, P.title, GPD.productName, profileU.nickname, P.createAt, " +
                    "TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, GPD.deadline) as remainDay, imagePath,  " +
                    "ST_DISTANCE_SPHERE(POINT(profileU.longitude, profileU.latitude), POINT(U.longitude, U.latitude)) AS distance " +
                "FROM Post P  " +
                    "JOIN User profileU ON P.userIdx = profileU.userIdx " +
                    "JOIN PostCategory PC ON P.categoryIdx = PC.categoryIdx  " +
                    "JOIN GroupPurchaseDetail GPD ON P.postIdx = GPD.postIdx  " +
                    "LEFT JOIN ( " +
                        "SELECT postIdx, MIN(imageIdx) as minIdx, path as imagePath " +
                        "FROM Image  " +
                        "GROUP BY postIdx  " +
                        ") MinIdImage ON P.postIdx = MinIdImage.postIdx " +
                    "JOIN User U ON U.userIdx = ? " +
                "WHERE FLOOR(P.categoryIdx / 10) = 2 AND profileU.userIdx = ? " +
                "ORDER BY P.createAt DESC " +
                "LIMIT ? OFFSET ?";

        return this.jdbcTemplate.query(Query, groupPurchaseRowMapper, userIdx, profileUserIdx, size, startIdx);
    }
}