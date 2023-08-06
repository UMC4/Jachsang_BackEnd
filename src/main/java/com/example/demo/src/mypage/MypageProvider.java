package com.example.demo.src.mypage;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetPageRes;
import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import com.example.demo.src.mypage.model.GetGroupPurchaseActivityRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MypageProvider {

    private final MypageDao mypageDao;

    @Autowired
    public MypageProvider(MypageDao mypageDao) { this.mypageDao = mypageDao; }

    private <T> GetPageRes<T> handleGetPageRes(List<T> items, int size) throws BaseException {
        if (items.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            boolean isLast = items.size() != size + 1;
            if (!isLast) {
                items.remove(items.size() - 1);
            }
            return new GetPageRes<>(items, isLast);
        }
    }

    public GetPageRes<GetCommunityActivityRes> getMyCommunityPosts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> items = mypageDao.getMyCommunityPosts(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetCommunityActivityRes> getMyCommunityComments(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> items = mypageDao.getMyCommunityComments(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetCommunityActivityRes> getMyCommunityLikes(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> items = mypageDao.getMyCommunityLikes(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetCommunityActivityRes> getMyCommunityHearts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> items = mypageDao.getMyCommunityHearts(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> items = mypageDao.getMyGroupPurchasePosts(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> items = mypageDao.getMyGroupPurchaseLikes(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> items = mypageDao.getMyGroupPurchaseParticipated(userIdx, startIdx, size + 1);
        return handleGetPageRes(items, size);
    }
}
