package com.example.demo.src.post;

import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.model.community.CommunityPost;
import com.example.demo.src.post.model.community.GetCommunityPostRes;
import com.example.demo.src.post.model.community.CommunityPostingReq;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.post.model.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePost;
import com.example.demo.src.post.model.groupPurchase.GroupPurchasePostingReq;
import com.example.demo.src.post.model.recipe.GetRecipePostRes;
import com.example.demo.src.post.model.recipe.RecipeInsertReq;
import com.example.demo.src.post.model.recipe.RecipePost;
import com.example.demo.src.post.model.recipe.RecipePostingReq;
import com.example.demo.src.privateMethod.Methods;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.security.Timestamp;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private ObjectMapper mapper;
    private Methods methods;
    @Autowired
    public PostDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.mapper = new ObjectMapper();
        methods = new Methods(dataSource);
    }

    // 글쓰기
    public PostingRes posting(int categoryIdx, HashMap<String,Object> postingReq) {
        try {
            System.setProperty("https.protocols", "TLSv1.0");
            // 입력받은 정보를 general information, specific information으로 구분하는 작업
            // 그 중에서 general information을 Post general에 담는 과정
            PostingReq general = new PostingReq(
                    (int) postingReq.get("userIdx"), (String) postingReq.get("title")
            );

            // Post table에 insert 하는 sql 문장과 그 파라미터, URL의 경우 'null'로 저장함.
            String sqlGeneral = "INSERT INTO Post(categoryIdx, userIdx, title, viewCount, likeCount, createAt, updateAt, url) VALUES (?,?,?,0,0,now(),now(),'null')";
            String category = CATEGORY.getName(categoryIdx);

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
            if (categoryIdx < 20) {
                CommunityPostingReq posting = new CommunityPostingReq(postIdx, (String) postingReq.get("contents"));
                sqlSpecific = "INSERT INTO CommunityDetail(communityDetailIdx, postIdx, contents, heartCount) VALUES (" + postIdx + "," + postIdx + ",?, 0)";
                paramSpecific = new Object[]{posting.getContents()};
            }
            //공동구매
            else if (categoryIdx < 30) {
                GroupPurchasePostingReq posting = new GroupPurchasePostingReq(postIdx, postingReq);
                sqlSpecific = "INSERT INTO GroupPurchaseDetail(groupPurchaseDetailIdx, postIdx, productName, productURL, singlePrice, deliveryFee, " +
                        "members, deadline,hasExtension, calculated) VALUES (" + postIdx + "," + postIdx + ",?,?,?,?,?,now(),false,false)";

                paramSpecific = new Object[]{
                        posting.getProductName(), posting.getProductURL(), posting.getSinglePrice(),
                        posting.getDeliveryFee(), posting.getMembers()
                };
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
            PostingRes postingRes = new PostingRes(postIdx, categoryIdx, category, general.getUserIdx(), general.getTitle(), "null");
            // 응답 반환

            //이미지 등록
            //여기서 이미지 없을 때 예외처리 해야됨
            postImage(postIdx, (List<String>) postingReq.get("paths"));

            return postingRes;
        } catch (Exception e){
            System.out.println(e.getStackTrace()+"\n알 수 없는 오류");
            return null;
        }
    }

    // 글보기
    public Object getPost (int categoryIdx, int userIdx, int postIdx) {
        // 세 게시판의 글을 한번에 처리하기 위한 변수 설정
        // 기본정보와 detail 정보 불러오기
        Object generalPost = methods._getPost(postIdx),
                detailPost = methods._getDetailPost(categoryIdx,postIdx);

        // 조회수 1 증가시키기 위해 sql문 작성 및 실행
        String viewUpdateSql = "UPDATE Post SET viewCount = viewCount+1 WHERE postIdx = "+postIdx;
        this.jdbcTemplate.update(viewUpdateSql);

        String getImageSql = "SELECT path FROM Image WHERE postIdx = "+postIdx;
        List<String> paths = this.jdbcTemplate.queryForList(getImageSql,String.class);

        String getCommentIdxSql = "SELECT commentIdx FROM Comment WHERE postIdx = "+postIdx;
        List<Integer> comments = this.jdbcTemplate.queryForList(getCommentIdxSql,Integer.class);

        boolean isScraped = this.methods._isLikedPost(userIdx, postIdx);
        boolean isHearted = this.methods._isHeartPost(userIdx, postIdx);

    // Post와 detail의 정보를 합친 후 리턴하기
        if (categoryIdx < 20) {
            GetCommunityPostRes result = new GetCommunityPostRes((Post)generalPost, (CommunityPost)detailPost,paths);
            result.setComments(comments);
            result.setScraped(isScraped);
            result.setLiked(isHearted);
            return result;
        }
        else if(categoryIdx < 30) {
            GetGroupPurchasePostRes result = new GetGroupPurchasePostRes((Post)generalPost,(GroupPurchasePost)detailPost,paths);
            result.setComments(comments);
            result.setScraped(isScraped);
            result.setLiked(isHearted);
            return result;
        }
        else if (categoryIdx == 30) {
            GetRecipePostRes result = new GetRecipePostRes((Post)generalPost, (RecipePost)detailPost,paths);
            result.setScraped(isScraped);
            return result;
        }
        else return null;
    }
    // 글 삭제
    public boolean deletePost(int postIdx) {
        // 결과 확인용 변수 선언 - Post(general)와 Detail 각각 준비한다.
        int general = 0, detail = 0, image = 0, like = 0, heart = 0, comment = 0, chatRoom = 0, chatUser = 0;
        // 유저 idx를 저장한다.
        // 게시판 종류를 확인한다.
        int boardIdx = 10*methods._getBoardIdx(postIdx);
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
        // HeartPost 내용을 삭제한다.
        String deleteHeartSql = "DELETE FROM HeartPost WHERE postIdx = "+postIdx;
        String deleteChatRoomSql = "DELETE FROM ChatRoom WHERE postIdx = "+postIdx;
        // HeartComment 내용을 삭제한다.
        String getPostIdxSql = "SELECT commentIdx FROM Comment WHERE postIdx = "+postIdx;
        List<Integer> comIdxs = this.jdbcTemplate.queryForList(getPostIdxSql,int.class);
        for(int i : comIdxs){
            String deleteHeartCommentSql = "DELETE FROM HeartComment WHERE commentIdx = "+i;
            this.jdbcTemplate.update(deleteHeartCommentSql);
        }
        // Comment 내용을 삭제한다.
        String deleteCommentSql = "DELETE FROM Comment WHERE postIdx = "+postIdx;
        // 마지막으로 Post 내용을 삭제한다.
        String deleteGeneralSql = "DELETE FROM Post WHERE postIdx = "+postIdx;
        // sql문 실행

        String getChatRoomIdx = "SELECT chatRoomIdx FROM ChatRoom WHERE postIdx = "+postIdx;
        List<Integer> chatRoomIdx = this.jdbcTemplate.queryForList(getChatRoomIdx,int.class);

        for(int idxx : chatRoomIdx){
            String getChatUserIdxSql = "SELECT chatUserIdx FROM ChatUser WHERE chatRoomIdx = "+idxx;
            List<Integer> chatUserIdx = this.jdbcTemplate.queryForList(getChatUserIdxSql,int.class);
            for(int chatIdxx : chatUserIdx) {
                String deleteChatCommentSql = "DELETE FROM ChatComment WHERE chatUserIdx = "+chatIdxx;
                this.jdbcTemplate.execute(deleteChatCommentSql);
            }
            String deleteChatUserSql = "DELETE FROM ChatUser WHERE chatRoomIdx = "+idxx;
            this.jdbcTemplate.execute(deleteChatUserSql);
        }

        chatRoom = this.jdbcTemplate.update(deleteChatRoomSql);
        image = this.jdbcTemplate.update(deleteImageSql);
        like = this.jdbcTemplate.update(deleteLikeSql);
        heart = this.jdbcTemplate.update(deleteHeartSql);
        this.jdbcTemplate.update(deleteCommentSql);
        detail = this.jdbcTemplate.update(deleteDetailSql);
        general = this.jdbcTemplate.update(deleteGeneralSql);

        if(detail * general == 0) return false;
        else return true;
    }
    // 글 수정
    public boolean updatePost(HashMap<String,Object> updateReq){
        // 입력받은 정보를 general information, specific information으로 구분하는 작업
        // 그 중에서 general information을 Post general에 담는 과정
        // postIdx, title을 저장
        int postIdx = (int)updateReq.get("postIdx");
        int boardIdx = methods._getBoardIdx(postIdx);
        int categoryIdx = methods._getCategoryIdx(postIdx);
        String title = (String)updateReq.get("title");

        // Post table에 insert 하는 sql 문장과 그 파라미터, URL의 경우 'null'로 저장함.
        String sqlGeneral = "UPDATE Post SET categoryIdx = ?, title = ?, updateAt = now() WHERE postIdx = "+postIdx;
        Object[] paramGeneral = {
                categoryIdx, title
        };
        // general 쿼리를 실행하는 부분
        this.jdbcTemplate.update(sqlGeneral, paramGeneral);

        // 공동구매, 커뮤니티, 레시피 세 경우에 대해, 각 테이블에 정보를 저장하기 위해 sql문과 param을 정의함.
        String sqlSpecific = "";
        Object[] paramSpecific = null;
        //커뮤니티
        if (boardIdx == 1) {
            sqlSpecific = "UPDATE CommunityDetail SET contents =  ? WHERE postIdx = "+postIdx;
            paramSpecific = new Object[]{
                    (String)updateReq.get("contents")
            };
        }
        //공동구매
        else if (boardIdx == 2) {
            sqlSpecific = "UPDATE GroupPurchaseDetail SET productName = ?, productURL =  ?, singlePrice = ?, deliveryFee = ?," +
                    " members = ?, deadline = ? WHERE postIdx = "+postIdx;
            paramSpecific = new Object[]{
                    (String)updateReq.get("productName"),
                    (String)updateReq.get("productURL"), (int)updateReq.get("singlePrice"),
                    (int)updateReq.get("deliveryFee"), (int)updateReq.get("members"),
                    (String)updateReq.get("deadline")
            };
        }
        // 오류 처리
        else {
            System.out.println("잘못된 카테고리 이름입니다.");
            return false;
        }
        // validation : 오류 처리
        if (sqlSpecific.equals("") || paramSpecific == null) {
            System.out.println("쿼리 또는 파라미터가 제대로 설정되지 않았습니다.");
            return false;
        }
        // 공동구매, 커뮤니티, 레시피 sql과 param을 이용해 쿼리문 실행
        this.jdbcTemplate.update(sqlSpecific, paramSpecific);
        // 반환할 응답 생성

        //이미지 등록
        //여기서 이미지 없을 때 예외처리 해야됨
        updateImage(postIdx,(List<String>)updateReq.get("paths"));

        return true;
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
        // 기록에 성공하고, 해당 게시글의 관심 지정한 수를 1 증가시키는 과정
        else {
            String likeCountIncreaseSql = "UPDATE Post SET likeCount = likeCount + 1 WHERE postIdx = "+likeReq.getPostIdx();
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
        // 게시글 좋아요 수 늘리는 sql 작성
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

    // 사진 첨부 메서드
    public boolean postImage(int postIdx, List<String> paths) {
        if(paths.isEmpty()) return true;
        String sql = "INSERT INTO Image(postIdx, path) VALUES";
        for (String path : paths) {
            sql += "(" + postIdx + ",\"" + path + "\"),";
        }
        sql = sql.substring(0,sql.length()-1);
        return this.jdbcTemplate.update(sql) == 1 ? true : false;
    }

    public boolean updateImage(int postIdx, List<String> paths) {
        String deleteImageSql = "DELETE FROM Image WHERE postIdx = "+postIdx;
        this.jdbcTemplate.update(deleteImageSql);
        return postImage(postIdx,paths);
    }

    public Methods _getMethods(){
        return this.methods;
    }

    public int deleteRecipes(){
        String getRecipePidx = "SELECT postIdx FROM RecipeDetail";
        List<Integer> postIdx = this.jdbcTemplate.queryForList(getRecipePidx,int.class);
        int count = 0;
        for(int p : postIdx){
            String deleteRecipeSql = "DELETE FROM RecipeDetail WHERE postIdx = " + p;
            String deletePostSql = "DELETE FROM Post WHERE postIdx = "+ p;
            this.jdbcTemplate.update(deleteRecipeSql);
            this.jdbcTemplate.update(deletePostSql);
            count++;
        }
        return count;
    }

    public GetUserDetailRes getUserDetail(int userIdx){
        String getUserDetailSql = "SELECT userName, nickName, loginId FROM User WHERE userIdx = "+userIdx;
        return this.jdbcTemplate.queryForObject(getUserDetailSql,(rs,rowNum)->new GetUserDetailRes(
                rs.getString("userName"),
                rs.getString("nickName"),
                rs.getString("loginId")
        ));
    }

    public int killAllCommunityPost(){
        String sql = "SELECT postIdx FROM Post WHERE categoryIdx < 20";
        List<Integer> postIdx = this.jdbcTemplate.queryForList(sql,int.class);
        int result = 0;
        for(int p : postIdx) {
            deletePost(p);
            result++;
        }
        return result;
    }
}