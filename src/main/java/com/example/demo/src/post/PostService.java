package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.privateMethod.Methods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;

@Service
public class PostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }
    
    //글쓰기 : 공동구매, 커뮤니티, 레시피 중 뭐든 실행할 수 있도록 구성함
    public PostingRes posting( int categoryIdx, HashMap<String,Object> postingReq) throws BaseException, SQLIntegrityConstraintViolationException {
        // Controller에서 타입에 따라 API를 나눌 것이므로 여기와 DAO에서는 메서드 하나로 처리 가능
        return this.postDao.posting(categoryIdx, postingReq);
    }

    //좋아요 표시
    public boolean scrapPost(LikeReq likeReq) throws BaseException {
        return this.postDao.scrapPost(likeReq);
    }

    public boolean cancelScrapPost(LikeReq likeReq)throws BaseException {
        return this.postDao.cancelScrapPost(likeReq);
    }
    public boolean heartPost(HeartPostReq heartPostReq) throws  BaseException {
        return this.postDao.heartPost(heartPostReq);
    }
    public boolean cancelHeartPost(HeartPostReq heartPostReq) throws BaseException{
        return this.postDao.cancelHeartPost(heartPostReq);
    }
    public boolean deletePost(int postIdx) throws BaseException{
        return this.postDao.deletePost(postIdx);
    }

    public boolean updatePost(HashMap<String,Object> updateReq) throws BaseException, SQLIntegrityConstraintViolationException{
        return this.postDao.updatePost(updateReq);
    }

    public Methods _getMethods(){
        return this.postDao._getMethods();
    }

    public int deleteRecipes() throws BaseException{
        return this.postDao.deleteRecipes();
    }

    public int deleteAllCommunity() throws BaseException{
        return this.postDao.killAllCommunityPost();
    }
}
