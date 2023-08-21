package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.generalModel.GetUserDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostProvider {
    private final PostDao postDao;

    @Autowired
    public PostProvider(PostDao postDao) {
        this.postDao = postDao;
    }

    public Object getPost(int categoryIdx, int userIdx, int postIdx) throws BaseException {
        return this.postDao.getPost(categoryIdx, userIdx, postIdx);
    }
    public int getLikeCount(int postIdx) throws BaseException{
        return this.postDao.getLikeCount(postIdx);
    }
    public GetUserDetailRes getUserDetail(int userIdx) throws BaseException {
        return this.postDao.getUserDetail(userIdx);
    }
}
