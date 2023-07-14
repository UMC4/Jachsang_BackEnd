package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    public int commenting (CommentingReq commentingReq) throws BaseException {
        return this.commentDao.commenting(commentingReq);
    }
    public int editComment(EditCommentReq editCommentReq) throws BaseException {
        return this.commentDao.editComment(editCommentReq);
    }
    public int likeComment(int commentIdx) throws BaseException{
        return this.commentDao.likeComment(commentIdx);
    }

    public int cancelLikeComment(int commentIdx) throws BaseException {
        return this.commentDao.cancelLikeComment(commentIdx);
    }
}
