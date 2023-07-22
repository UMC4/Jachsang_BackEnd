package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
import com.example.demo.src.comment.model.LikeReq;
import com.example.demo.src.comment.model.ReplyReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    public int commenting (CommentingReq commentingReq) throws SQLIntegrityConstraintViolationException,BaseException {
        return this.commentDao.commenting(commentingReq);
    }
    public int editComment(EditCommentReq editCommentReq) throws SQLIntegrityConstraintViolationException, BaseException {
        return this.commentDao.editComment(editCommentReq);
    }

    public int deleteComment(int commentIdx) throws BaseException{
        return this.commentDao.deleteComment(commentIdx);
    }

    public int likeComment(LikeReq likeReq) throws BaseException{
        return this.commentDao.likeComment(likeReq);
    }
    public int cancelLikeComment(LikeReq likeReq) throws BaseException {
        return this.commentDao.cancelLikeComment(likeReq);
    }
    public int replying(ReplyReq replyReq) throws BaseException {
        return this.commentDao.replying(replyReq);
    }

    public boolean _isExistPostIdx(int postIdx){
        return this.commentDao._isExistPostIdx(postIdx);
    }

    public boolean _isExistCommentIdx(int commentIdx){
        return this.commentDao._isExistCommentIdx(commentIdx);
    }
    public int _getUserIdxByCommentIdx(int commentIdx){
        return this.commentDao._getUserIdxByCommentIdx(commentIdx);
    }
}
