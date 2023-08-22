package com.example.demo.src.privateMethod;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.community.CommunityPost;
import com.example.demo.src.post.model.generalModel.Post;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePost;
import com.example.demo.src.post.model.recipe.RecipeInsertReq;
import com.example.demo.src.post.model.recipe.RecipePost;
import com.example.demo.src.report.model.CheckReportReq;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;
import java.sql.SQLException;
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
                rs.getString("createAt"),
                rs.getString("updateAt"),
                rs.getString("url")));
    }
    public Object _getDetailPost(int categoryIdx, int postIdx){
        // 카테고리 detail 정보 불러오기
        Object detailPost;
        if (categoryIdx < 20) {
            String qry = "SELECT * FROM CommunityDetail WHERE postIdx = "+postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new CommunityPost(
                    rs.getInt("communityDetailIdx"),
                    rs.getInt("heartCount"),
                    rs.getString("contents")
            ));
            // Post와 detail의 정보를 합친 후 리턴하기
            return (CommunityPost)detailPost;
        }
        //공동구매 detail 정보 불러오기
        else if(categoryIdx < 20) {
            String qry = "SELECT * FROM GroupPurchaseDetail WHERE postIdx = "+postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new GroupPurchasePost(
                    rs.getInt("groupPurchaseDetailIdx"),
                    rs.getString("productName"),
                    rs.getString("productURL"),
                    rs.getInt("heartCount"),
                    rs.getDouble("singlePrice"),
                    rs.getDouble("deliveryFee"),
                    rs.getInt("members"),
                    rs.getTimestamp("deadline"),
                    rs.getBoolean("hasExtension"),
                    rs.getBoolean("calculated")
            ));
            return (GroupPurchasePost)detailPost;
        }
        // 레시피 detail 정보 불러오기
        else if (categoryIdx == 30) {
            String qry = "SELECT * FROM RecipeDetail WHERE postIdx = " + postIdx;
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new RecipePost(
                    rs.getInt("recipeDetailIdx"),
                    rs.getString("mainImageUrl"),
                    rs.getString("ingredients"),
                    rs.getString("description")
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
            return this.jdbcTemplate.queryForObject(sql,int.class) == commentIdx ? true:false;
        } catch (Exception e) {
            return false;
        }
    }
    public int _getUserIdxByCommentIdx(int commentIdx){
        String getUserIdxSql = "SELECT userIdx FROM Comment WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxSql,int.class);
    }
    public boolean _isExistReport(CheckReportReq checkReportReq) {
        try {
            String checkSql = "";
            if (checkReportReq.getKind() == 40) {
                checkSql = "SELECT reportIdx FROM Report WHERE reportingUserIdx = " + checkReportReq.getUserIdx() + " AND contentsKind = " + checkReportReq.getKind();
            } else if (checkReportReq.getKind() != 40) {
                checkSql = "SELECT reportIdx FROM Report WHERE reportingUserIdx = " +
                        checkReportReq.getUserIdx() +
                        " AND contentsKind = " +
                        checkReportReq.getKind() +
                        " AND reportedContentsIdx = " +
                        checkReportReq.getContentsIdx();
            }
            return this.jdbcTemplate.queryForObject(checkSql, int.class) > 0 ? true : false;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (IncorrectResultSizeDataAccessException e) {
            return true;
        }
    }
    public boolean _isAdmin(int userIdx){
        String checkAdminSql = "SELECT role FROM User WHERE userIdx = "+userIdx;
        String role = this.jdbcTemplate.queryForObject(checkAdminSql,String.class);
        if(role.toLowerCase().equals("admin")) return true;
        else return false;
    }
    public boolean _isLikedPost(int userIdx, int postIdx){
        try {

            String checkLikedPost = "SELECT userIdx FROM LikedPost WHERE postIdx = ? AND userIdx = ?";
            Object[] param = {postIdx, userIdx};
            int result = this.jdbcTemplate.queryForObject(checkLikedPost, param, int.class);
            return result > 0 ? true : false;

        } catch(Exception e){
            return false;
        }
    }
    public boolean _isHeartPost(int userIdx, int postIdx){
        try{
            String checkHeartPost = "SELECT * FROM HeartPost WHERE postIdx = ? AND userIdx = ?";
            Object[] param = {postIdx, userIdx};
            int result = this.jdbcTemplate.queryForObject(checkHeartPost, param, int.class);
            return result > 0 ? true : false;
        }
        catch (Exception e) {
            return false;
        }
    }
    public void recipeTest() throws BaseException {
        com.example.demo.src.recipeCrawl.Methods m = new com.example.demo.src.recipeCrawl.Methods();
        List<RecipeInsertReq> list = m.getRecipe();
        for(RecipeInsertReq r : list){
            _insertRecipeData(r);
        }
    }
    public int _insertRecipeData(RecipeInsertReq recipeInsertReq){
        String insertOnPostSql = "INSERT INTO Post(categoryIdx,userIdx,title,url) VALUES(30,2,?,?)";
        String insertOnRecipeSql = "INSERT INTO RecipeDetail(postIdx, ingredients,description,mainImageUrl,originUrl) VALUES(?,?,?,?,?)";
        Object[] postParam = {
                recipeInsertReq.getTitle(), recipeInsertReq.getUrl()
        };
        this.jdbcTemplate.update(insertOnPostSql ,postParam);
        String sql = "SELECT postIdx FROM Post WHERE categoryIdx = 30 " + "AND userIdx = 2 "+
                "AND title = \""+ recipeInsertReq.getTitle()+"\"";
        List<Integer> postIdx = this.jdbcTemplate.queryForList(sql,int.class);

        Object[] recipeParam = {
                postIdx.get(0), recipeInsertReq.getIngredients(), recipeInsertReq.getDescription(),
                recipeInsertReq.getMainImageUrl(), recipeInsertReq.getOriginUrl()
        };
        return this.jdbcTemplate.update(insertOnRecipeSql,recipeParam);
    }
}
