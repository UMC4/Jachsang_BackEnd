package com.example.demo.src.post;

import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
import com.example.demo.src.post.model.community.CommunityPost;
import com.example.demo.src.post.model.community.GetCommunityPostRes;
import com.example.demo.src.post.model.community.CommunityPostingReq;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.post.model.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePost;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePostingReq;
import com.example.demo.src.post.model.recipe.GetRecipePostRes;
import com.example.demo.src.post.model.recipe.RecipePost;
import com.example.demo.src.post.model.recipe.RecipePostingReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private ObjectMapper mapper;
    @Autowired
    public PostDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.mapper = new ObjectMapper();
    }

    // 글쓰기
    public PostingRes posting(int boardIdx, int categoryIdx, HashMap<String,Object> postingReq) {
        // 입력받은 정보를 general information, specific information으로 구분하는 작업
        // 그 중에서 general information을 Post general에 담는 과정
        PostingReq general = new PostingReq(
                (int)postingReq.get("userIdx"), (String)postingReq.get("title")
        );
        
        // Post table에 insert 하는 sql 문장과 그 파라미터, URL의 경우 'null'로 저장함.
        String sqlGeneral = "INSERT INTO Post(categoryIdx, userIdx, title, viewCount, likeCount, createAt, updateAt, url) VALUES (?,?,?,0,0,now(),now(),'null')";
        Object[] paramGeneral = {
                categoryIdx, general.getUserIdx(), general.getTitle()
        };
        // general 쿼리를 실행하는 부분
        this.jdbcTemplate.update(sqlGeneral, paramGeneral);
        // postIdx 값을 쉽게 사용하기 위해 정의함.
        String lastInsertIdQuery = "select last_insert_id()";
        int postIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
        // 공동구매, 커뮤니티, 레시피 세 경우에 대해, 각 테이블에 정보를 저장하기 위해 sql문과 param을 정의함.
        String sqlSpecific = "";
        Object[] paramSpecific = null;
        //커뮤니티
        if (boardIdx == 1) {
            CommunityPostingReq posting = new CommunityPostingReq(postIdx, (String)postingReq.get("contents"));
            sqlSpecific = "INSERT INTO CommunityDetail(communityDetailIdx, postIdx, contents, heartCount) VALUES (" + postIdx + "," + postIdx + ",?, 0)";
            paramSpecific = new Object[]{posting.getContents()};
        }
        //공동구매
        else if (boardIdx == 2) {
            GroupPurchasePostingReq posting = new GroupPurchasePostingReq(postIdx, postingReq);
            sqlSpecific = "INSERT INTO GroupPurchaseDetail(groupPurchaseDetailIdx, postIdx, productName, productURL, singlePrice, deliveryFee, " +
                    "members, deadline,hasExtension, calculated) VALUES (" + postIdx + "," + postIdx + ",?,?,?,?,?,?,false,false)";

            paramSpecific = new Object[]{
                    posting.getProductName(), posting.getProductURL(), posting.getSinglePrice(),
                    posting.getDeliveryFee(), posting.getMembers(), posting.getDeadline()
            };
        }
        //레시피
        else if (boardIdx == 3) {
            RecipePostingReq posting = mapper.convertValue(postingReq, RecipePostingReq.class);
            sqlSpecific = "INSERT INTO RecipeDetail(recipeDetailIdx,postIdx, contents, tag) VALUES(" + postIdx + "," + postIdx + ",?,?)";
            paramSpecific = new Object[]{(String)postingReq.get("contents"), (String)postingReq.get("tag")};
        }
        // 오류 처리
        else {
            System.out.println("잘못된 카테고리 이름입니다.");
            return null;
        }
        // validation : 오류 처리
        if (sqlSpecific.equals("") || paramSpecific == null) {
            System.out.println("쿼리 또는 파라미터가 제대로 설정되지 않았습니다.");
            return null;
        }
        // 공동구매, 커뮤니티, 레시피 sql과 param을 이용해 쿼리문 실행
        this.jdbcTemplate.update(sqlSpecific, paramSpecific);
        // 반환할 응답 생성
        PostingRes postingRes = new PostingRes(postIdx, categoryIdx, general.getCategory(), general.getUserIdx(), general.getTitle(), "null");
        // 응답 반환
        
        //이미지 등록
        //여기서 이미지 없을 때 예외처리 해야됨
        postImage(postIdx,(List<String>)postingReq.get("paths"));

        return postingRes;
    }

    // 글보기
    public Object getPost(int categoryIdx, GetPostReq getPostReq) {
        // 세 게시판의 글을 한번에 처리하기 위한 변수 설정
        // 기본정보와 detail 정보 불러오기
        Object generalPost = _getPost(getPostReq.getPostIdx()),
                detailPost = _getDetailPost(categoryIdx,getPostReq.getPostIdx());
        // 조회수 1 증가시키기 위해 sql문 작성 및 실행
        String viewUpdateSql = "UPDATE Post set viewCount = viewCount+1 WHERE postIdx = "+getPostReq.getPostIdx();
        this.jdbcTemplate.update(viewUpdateSql);

        String getImageSql = "SELECT path FROM Image WHERE imageIdx = "+getPostReq.getPostIdx();
        List<String> paths = this.jdbcTemplate.query(getImageSql, (rs,rowNum) -> new String(
                rs.getString("path")
        ));
        // Post와 detail의 정보를 합친 후 리턴하기
        if (categoryIdx == 10) {
            return new GetCommunityPostRes((Post)generalPost, (CommunityPost)detailPost,paths);
        }
        else if(categoryIdx == 20) {
            return new GetGroupPurchasePostRes((Post)generalPost,(GroupPurchasePost)detailPost,paths);
        }
        else if (categoryIdx == 30) {
            return new GetRecipePostRes((Post)generalPost, (RecipePost)detailPost,paths);
        }
        else return null;
    }
    // 글 삭제
    public boolean deletePost(int postIdx) {
        // 결과 확인용 변수 선언 - Post(general)와 Detail 각각 준비한다.
        int general = 0, detail = 0, image = 0, like = 0;
        // 게시판 종류를 확인한다.
        int boardIdx = _getBoardIdx(postIdx);;
        // 게시판 종류를 저장한다.
        String board = boardIdx == 10 ? "Community" :
                (boardIdx == 20 ? "GroupPurchase" :
                        (boardIdx == 30 ? "Recipe" : null));
        // 잘못된 게시판 이름 입력 시
        if (board == null) return false;
        // Detail의 내용을 먼저 없애야 한다 (ANOMALY 예방)
        String deleteDetailSql = "DELETE FROM "+board+"Detail WHERE postIdx = "+postIdx;
        // Image 내용을 삭제한다.
        String deleteImageSql = "DELETE FROM Image WHERE postIdx = "+postIdx;
        // LikedTable 내용을 삭제한다.
        String deleteLikeSql = "DELETE FROM LikedPost WHERE postIdx = "+postIdx;
        // 마지막으로 Post 내용을 삭제한다.
        String deleteGeneralSql = "DELETE FROM Post WHERE postIdx = "+postIdx;
        // sql문 실행
        detail = this.jdbcTemplate.update(deleteDetailSql);
        general = this.jdbcTemplate.update(deleteGeneralSql);
        image = this.jdbcTemplate.update(deleteImageSql);
        like = this.jdbcTemplate.update(deleteDetailSql);

        if(detail * general * image * like == 1) return true;
        else return false;
    }
    // 글 수정
    public Object editPost(EditPostReq editPostReq){
        // postIdx값 저장
        int postIdx = editPostReq.getPostIdx();
        // board값 추출
        String board = _getBoardName(_getBoardIdx(postIdx));
        // Post 수정 sql
        String postEditSql = "UPDATE Post SET postIdx = "+postIdx;
        String postEditCondition = " WHERE postIdx = "+postIdx;
        
        // Detail 수정 sql
        String frontSql = "UPDATE "+board+"Detail";
        String behindSql = " SET postIdx"+" = "+postIdx;
        String condition = " WHERE postIdx = "+postIdx;

        // 수정 과정
        for(ParamPack parameter : editPostReq.getParams()){
            // parameter를 param(칼럼)과 value(값)으로 구분
            String param = parameter.getParamName();
            Object value = parameter.getParamValue();
            // Post의 데이터 중 변경 가능 사항 sql문 작성 : 제목과 카테고리 밖에 없음
            if(param.equals("title") || param.equals("categoryIdx")) {
                // String형 자료는 ""로 감싸야하므로 처리함
                if(param.equals("title")) postEditSql = postEditSql +", "+ param + " = " + "\"" + value +"\"";
                // 아닌건 그냥 추가
                else postEditSql = postEditSql + ", " + param + " = "+ value;
            }
            // Detail의 데이터 변경 sql문 작성
            else {
                // String 자료
                if(param.equals("contents") || param.equals("url") || param.equals("productName") ||
                        param.equals("productUrl") || param.equals("tag")) {
                    behindSql = behindSql +", "+ param + " = " + "\"" + value +"\"" ;
                }
                // 아닌 것
                else behindSql = behindSql+ ", " + param+" = "+value ;
            }
        }
        // sql문 합치기
        postEditSql = postEditSql + ", updateAt = now()" + postEditCondition;
        String detailEditSql = frontSql + behindSql + condition;
        // sql문 실행하기
        this.jdbcTemplate.update(postEditSql+";");
        this.jdbcTemplate.update(detailEditSql+";");
        return "성공했습니다";
    }

    // 관심목록 추가
    public boolean scrapPost(LikeReq likeReq){
        // 어떤 유저가 어떤 게시글에 스크랩을 눌렀는가를 LikedPost 테이블에 기록하는 과정
        String sql = "INSERT INTO LikedPost(postIdx, userIdx) VALUES (?,?)";
        Object[] param = {likeReq.getPostIdx(),likeReq.getUserIdx()};
        // 기록에 실패한 경우
        if(this.jdbcTemplate.update(sql,param) == 0) {
            System.out.println("LikedPost 테이블에 기록하지 못했습니다.");
            return false;
        }
        // 기록에 성공하고, 해당 게시글의 좋아요 수를 1 증가시키는 과정
        else {
            String likeCountIncreaseSql = "UPDATE CommunityDetail SET likeCount = likeCount + 1 WHERE postIdx = "+likeReq.getPostIdx();
            // 좋아요 수를 증가시키는 것을 실패한 경우
            if(this.jdbcTemplate.update(likeCountIncreaseSql) == 0) return false;
                // 좋아요 수를 증가시키고, 해당 게시글의 좋아요 수를 불러오는 과정 (리턴과 관련)
            else {
                return true;
            }
        }
    }
    //관심목록에서 제거
    public boolean cancelScrapPost(LikeReq likeReq){
        String subSql = "UPDATE Post SET likeCount = likeCount - 1 WHERE postIdx = "+likeReq.getPostIdx();
        String delSql = "DELETE FROM LikedPost WHERE postIdx = "+likeReq.getPostIdx()+
                " AND userIdx = "+likeReq.getUserIdx();
        if(this.jdbcTemplate.update(subSql) == 0) return false;
        // 예외처리 필요
        if(this.jdbcTemplate.update(delSql) == 0) return false;
        // 예외처리 필요
        return true;
    }
    // 좋아요 누르기
    public boolean heartPost(HeartPostReq heartPostReq){
        // 게시글 공감 수 늘리는 sql 작성
        String countSql = "UPDATE CommunityDetail SET heartCount = heartCount+1 WHERE postIdx = "+heartPostReq.getPostIdx();
        String addSql = "INSERT INTO HeartPost(postIdx,userIdx) VALUES (?,?)";
        Object[] param = {heartPostReq.getPostIdx(),heartPostReq.getUserIdx()};
        if(this.jdbcTemplate.update(countSql) == 0) return false;
        // 예외처리 필요
        if(this.jdbcTemplate.update(addSql,param) == 0) return false;

        return true;
    }
    // 좋아요(하트) 취소
    public boolean cancelHeartPost(HeartPostReq heartPostReq){
        String countSql = "UPDATE CommunityDetail SET heartCount = heartCount-1 WHERE postIdx = "+heartPostReq.getPostIdx();
        String delSql = "DELETE From HeartPost WHERE postIdx = "+heartPostReq.getPostIdx()
                +" AND userIdx = "+heartPostReq.getUserIdx();
        if(this.jdbcTemplate.update(countSql) == 0) return false;
        // 예외처리 필요
        if(this.jdbcTemplate.update(delSql) == 0) return false;
        // 예외처리 필요
        return true;
    }

    // 게시글이 관심목록으로 등록된 수 반환 
    public int getLikeCount(int postIdx) {
        int likeCount = -1;
        try{
            //sql 작성
            String sql = "SELECT likeCount FROM Post WHERE postIdx = "+postIdx;
            likeCount = this.jdbcTemplate.queryForObject(sql,int.class);
            }catch(Exception e){
                return -1;
            }
        return likeCount;
    }
    
    // 공구 - 마감기한 연장
    public int extendDeadLine(int postIdx){
        try{
            String checkSql = "SELECT hasExtension FROM GroupPurchaseDetail WHERE postIdx = "+postIdx;
            if(this.jdbcTemplate.queryForObject(checkSql,int.class) == 0) return 0;

            // sql 작성
            String updateSql =
                    "UPDATE GroupPurchaseDetail SET deadline =  " +
                    "TIMESTAMPADD(WEEK,1,deadline), hasExtension = true WHERE postIdx = "+postIdx;
            return this.jdbcTemplate.update(updateSql);
        } catch (Exception e) {
            return 0;
        }
    }



    //////////////////////// 내부 메서드 //////////////////////////////

    // 사진 첨부 메서드
    public boolean postImage(int postIdx, List<String> paths) {
        String sql = "INSERT INTO Image(postIdx, path) VALUES";
        for (String path : paths) {
            sql += "(" + postIdx + ",\"" + path + "\"),";
        }
        sql = sql.substring(0,sql.length()-1);
        return this.jdbcTemplate.update(sql) == 1 ? true : false;
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

    public int _getBoardIdx(int postIdx){
        String sql = "SELECT categoryIdx FROM Post WHERE postIdx = "+postIdx;
        return this.jdbcTemplate.queryForObject(sql,int.class)/10;
    }

    public String _getBoardName(int categoryIdx) {
        return categoryIdx == 1 ? "Community" : (categoryIdx == 2 ? "GroupPurchase" : "Recipe");
    }

    public boolean _isExtended(int postIdx){
        String sql = "SELECT hasExtension FROM GroupPurchase WHERE postIdx = "+postIdx;
        boolean result = this.jdbcTemplate.queryForObject(sql,boolean.class);
        return result;
    }
    public int _isExistPostIdx(int postIdx) {
        String sql = "SELECT postIdx FROM Post WHERE postIdx = "+postIdx;
        try{
            return this.jdbcTemplate.queryForObject(sql,int.class);
        } catch (Exception e) {
            return -1;
        }
    }
}