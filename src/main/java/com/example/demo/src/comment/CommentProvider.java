package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentProvider {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    public CommentProvider(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public Comment getComment(int commentIdx) throws BaseException {
        return this.commentDao.getComment(commentIdx);
    }
}
