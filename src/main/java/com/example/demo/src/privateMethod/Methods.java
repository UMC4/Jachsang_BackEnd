package com.example.demo.src.privateMethod;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.community.CommunityPost;
import com.example.demo.src.post.model.generalModel.Post;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePost;
import com.example.demo.src.post.model.recipe.RecipeInsertReq;
import com.example.demo.src.post.model.recipe.RecipePost;
import com.example.demo.src.report.model.CheckReportReq;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


public class Methods {

    private final JdbcTemplate jdbcTemplate;

    public Methods(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Post _getPost(int postIdx){
        String sql = "SELECT * FROM Post WHERE postIdx = " + postIdx;
        return this.jdbcTemplate.queryForObject(sql, (rs,rowNum)-> new Post(
                rs.getInt("postIdx"),
                rs.getInt("categoryIdx"),
                rs.getInt("userIdx"),
                rs.getString("title"),
                rs.getInt("viewCount"),
                rs.getInt("likeCount"),
                rs.getTimestamp("createAt"),
                rs.getTimestamp("updateAt"),
                rs.getString("url")));
    }
    public Object _getDetailPost(int boardIdx, int postIdx){
        // 공동구매 detail 정보 불러오기
        Object detailPost;
        if(boardIdx == 20) {
            String qry = "SELECT * FROM GroupPurchaseDetail WHERE postIdx = "+postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new GroupPurchasePost(
                    rs.getInt("groupPurchaseDetailIdx"),
                    rs.getString("productName"),
                    rs.getString("productURL"),
                    rs.getDouble("singlePrice"),
                    rs.getDouble("deliveryFee"),
                    rs.getInt("members"),
                    rs.getTimestamp("deadline"),
                    rs.getBoolean("hasExtension"),
                    rs.getBoolean("calculated")
            ));
            return (GroupPurchasePost)detailPost;
        }
        // 커뮤니티 detail 정보 불러오기
        else if (boardIdx == 10) {
            String qry = "SELECT * FROM CommunityDetail WHERE postIdx = "+postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new CommunityPost(
                    rs.getInt("communityDetailIdx"),
                    rs.getString("contents")
            ));
            // Post와 detail의 정보를 합친 후 리턴하기
            return (CommunityPost)detailPost;
        }
        // 레시피 detail 정보 불러오기
        else if (boardIdx == 30) {
            String qry = "SELECT * FROM RecipeDetail WHERE postIdx = " + postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new RecipePost(
                    rs.getInt("recipeDetailIdx"),
                    rs.getString("contents"),
                    rs.getString("tag")
            ));
            return (RecipePost)detailPost;
        }
        else return null;
    }
    public int _getCategoryIdx(int postIdx){
        String sql = "SELECT categoryIdx FROM Post WHERE postIdx = "+postIdx;
        return this.jdbcTemplate.queryForObject(sql,int.class);
    }
    public int _getBoardIdx(int postIdx){
        return this._getCategoryIdx(postIdx)/10;
    }
    public boolean _isExistPostIdx(int postIdx) {
        String sql = "SELECT postIdx FROM Post WHERE postIdx = "+postIdx;
        try{
            return this.jdbcTemplate.queryForObject(sql,int.class) == postIdx ? true:false;
        } catch (Exception e) {
            return false;
        }
    }
    public String _getUserRole(int userIdx){
        String getUserRoleIdxSql = "SELECT role FROM User Where userIdx = "+userIdx;
        return this.jdbcTemplate.queryForObject(getUserRoleIdxSql,String.class);
    }
    public int _getUserIdxByPostIdx(int postIdx){
        String getUserIdxSql = "SELECT userIdx FROM Post WHERE postIdx = "+postIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxSql,int.class);
    }
    public int _getOriginIdxOf(int commentIdx){
        String getOriginIdxSql = "SELECT originIdx From Comment WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getOriginIdxSql,int.class);
    }
    public boolean _isExistCommentIdx(int commentIdx) {
        String sql = "SELECT commentIdx FROM Comment WHERE commentIdx = "+commentIdx;
        try{
            return this.jdbcTemplate.queryForObject(sql,int.class) == 1 ? true:false;
        } catch (Exception e) {
            return false;
        }
    }
    public int _getUserIdxByCommentIdx(int commentIdx){
        String getUserIdxSql = "SELECT userIdx FROM CommentIdx WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxSql,int.class);
    }
    public boolean _isExistReport(CheckReportReq checkReportReq){
        String checkSql = "";
        if(checkReportReq.getKind() == 40) {
            checkSql = "SELECT reportReq FROM Report WHERE reportingUserIdx = "+checkReportReq.getUserIdx()+", contentsKind = 40";
        }
        else if (checkReportReq.getKind() != 40){
            checkSql = "SELECT reportReq FROM Report WHERE reportingUserIdx = " +
                    checkReportReq.getUserIdx() +
                    ", contentsKind = " +
                    checkReportReq.getKind() +
                    ", reportedContentsIdx = " +
                    checkReportReq.getContentsIdx();
        }
        return this.jdbcTemplate.queryForObject(checkSql,int.class) > 0 ? true : false;
    }
}
