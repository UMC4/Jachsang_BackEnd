package com.example.demo.src.post;

import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.community.CommunityPost;
import com.example.demo.src.post.community.GetCommunityPostRes;
import com.example.demo.src.post.generalModel.GetPostReq;
import com.example.demo.src.post.generalModel.GetGeneralPost;
import com.example.demo.src.post.generalModel.PostingReq;
import com.example.demo.src.post.generalModel.PostingRes;
import com.example.demo.src.post.community.CommunityPostingReq;
import com.example.demo.src.post.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.groupPurchase.GroupPurchasePost;
import com.example.demo.src.post.groupPurchase.GroupPurchasePostingReq;
import com.example.demo.src.post.likeModel.LikeReq;
import com.example.demo.src.post.recipe.GetRecipePostRes;
import com.example.demo.src.post.recipe.RecipePost;
import com.example.demo.src.post.recipe.RecipePostingReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private ObjectMapper mapper;
    @Autowired
    public PostDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.mapper = new ObjectMapper();
    }

    /*	3.1 커뮤니티
	3.1.1 글쓰기
	- 주제, 제목, 내용, 사진 등록 (validation은 프론트에서)
    Post : postIdx; categoryIdx; userIdx; title; viewCount; likeCount; createAt; updateAt; url;
    GroupPurchaseDetail : groupPurchaseDetailIdx; postIdx; productName; productURL; singlePrice;
                          deliveryFee; members; deadline;hasExtension; calculated;
    CommunityDetail :     communityDetailIdx; postIdx; contents;
    RecipeDetail :        recipeDetailIdx; postIdx; contents; tag;
    PostingRes :          categoryIdx; userIdx; title; createAt; url;
     */

    // TODO : 사진 올리기 추가해야한다.
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
            sqlSpecific = "INSERT INTO CommunityDetail(communityDetailIdx, postIdx, contents) VALUES (" + postIdx + "," + postIdx + ",?)";
            paramSpecific = new Object[]{posting.getContents()};
        }
        //공동구매
        else if (boardIdx == 2) {
            GroupPurchasePostingReq posting = new GroupPurchasePostingReq(postingReq);
            sqlSpecific = "INSERT INTO GroupPurchaseDetail(groupPurchaseDetailIdx, postIdx, productName, productURL, singlePrice, deliveryFee, " +
                    "members, deadline,hasExtension, calculated) VALUES (" + postIdx + "," + postIdx + ",?,?,?,?,?,?,?,?)";
            paramSpecific = new Object[]{posting.getProductName(), posting.getProductURL(), posting.getSinglePrice(),
                    posting.getDeliveryFee(), posting.getMembers(), posting.getDeadline(), posting.isHasExtension(), posting.isCalculated()
            };
        }
        //레시피
        else if (boardIdx == 3) {
            RecipePostingReq posting = mapper.convertValue(postingReq, RecipePostingReq.class);
            sqlSpecific = "INSERT INTO RecipeDetail(recipeDetailIdx,postIdx, contents, tag) VALUES(" + postIdx + "" + postIdx + ",?,?)";
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
        return postingRes;
    }

    /*//      3.1.2 글보기
//		- 카테고리, 글쓴이, 제목, 내용, 공감, 관심, 조회 표시
//		- 글 내용 달라는 요청 시 조회수 1 증가시킨다.
//		- 공감하기, 관심표시하기 기능
//		- 공유하기 (URL 존재) 글 삭제
//		- 신고 기능/채팅하기
*/

    //TODO : 사진 불러오기 추가해야 한다.

    // 글보기
    public Object getPost(int categoryIdx, GetPostReq getPostReq) {
        // 세 게시판의 글을 한번에 처리하기 위한 변수 설정
        Object generalPost, detailPost;
        // 기본 정보(Post의 정보)를 불러오기 위한 sql문
        String sql = "SELECT * FROM Post WHERE postIdx = "+getPostReq.getPostIdx();

        generalPost = this.jdbcTemplate.queryForObject(sql, (rs,rowNum)-> new GetGeneralPost(
                rs.getInt("postIdx"),
                rs.getInt("categoryIdx"),
                rs.getInt("userIdx"),
                rs.getString("title"),
                rs.getInt("viewCount"),
                rs.getInt("likeCount"),
                rs.getTimestamp("createAt"),
                rs.getTimestamp("updateAt"),
                rs.getString("url")));
        
        // 조회수 1 증가시키기 위해 sql문 작성 및 실행
        String viewUpdateSql = "UPDATE Post set viewCount = viewCount+1 WHERE postIdx = "+getPostReq.getPostIdx();
        this.jdbcTemplate.update(viewUpdateSql);

        // 공동구매 detail 정보 불러오기
        if(categoryIdx == 20) {
            String qry = "SELECT * FROM GroupPurchaseDetail WHERE postIdx = "+getPostReq.getPostIdx();
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
            // Post와 detail의 정보를 합친 후 리턴하기
            return new GetGroupPurchasePostRes((GetGeneralPost)generalPost,(GroupPurchasePost)detailPost);
        }
        // 커뮤니티 detail 정보 불러오기
        else if (categoryIdx == 10) {
            String qry = "SELECT * FROM CommunityDetail WHERE postIdx = "+getPostReq.getPostIdx();
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new CommunityPost(
                    rs.getInt("communityDetailIdx"),
                    rs.getString("contents")
            ));
            // Post와 detail의 정보를 합친 후 리턴하기
            return new GetCommunityPostRes((GetGeneralPost)generalPost, (CommunityPost)detailPost);
        }
        // 레시피 detail 정보 불러오기
        else if (categoryIdx == 30) {
            String qry = "SELECT * FROM RecipeDetail WHERE postIdx = "+getPostReq.getPostIdx();
            detailPost = this.jdbcTemplate.queryForObject(qry, (rs, rowNum) -> new RecipePost(
                    rs.getInt("recipeDetailIdx"),
                    rs.getString("contents"),
                    rs.getString("tag")
            ));
            // Post와 detail의 정보를 합친 후 리턴하기
            return new GetRecipePostRes((GetGeneralPost)generalPost, (RecipePost)detailPost);
        }
        else return null;
    }


//		- 관심표시하기 기능

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
            String likeCountIncreaseSql = "UPDATE Post SET likeCount = likeCount + 1 WHERE postIdx = "+likeReq.getPostIdx();
            // 좋아요 수를 증가시키는 것을 실패한 경우
            if(this.jdbcTemplate.update(likeCountIncreaseSql) == 0) return false;
            // 좋아요 수를 증가시키고, 해당 게시글의 좋아요 수를 불러오는 과정
            else {
                return true;
            }
        }
    }

//		- (내 글인 경우) 마감 기한 1주일 증가 (1회용)
//		- 참여하기 기능
//		- 공유, 채팅, 신고

//	3.3 레시피
//		3.3.1 글 보기
//		- 사진, 재료, 조리순서
//		3.3.2 스크랩한 레시피
//			3.3.2.1 글 목록 보기
//			- 제목, 해시태그, 사진 보기
//			- 공감 취소 / 재공감 기능
}