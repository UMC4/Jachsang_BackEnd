package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.generalModel.GetPostReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostProvider {
    private final PostDao postDao;

    @Autowired
    public PostProvider(PostDao postDao) {
        this.postDao = postDao;
    }

    public Object getPost(int categoryIdx, GetPostReq getPostReq) throws BaseException {
        return this.postDao.getPost(categoryIdx,getPostReq);
    }
    public int getLikeCount(int postIdx) throws BaseException{
        return this.postDao.getLikeCount(postIdx);
    }

    public int _getBoardIdxOf(int postIdx){
        return this.postDao._getBoardIdx(postIdx);
    }
    public int _isExistPostIdx(int postIdx){
        return this.postDao._isExistPostIdx(postIdx);
    }
}
