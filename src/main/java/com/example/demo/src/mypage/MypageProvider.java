package com.example.demo.src.mypage;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
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

    public List<GetCommunityActivityRes> getMyCommunityPosts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> communityList = mypageDao.getMyCommunityPosts(userIdx, startIdx, size);
        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityComments(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> communityList = mypageDao.getMyCommunityComments(userIdx, startIdx, size);
        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityLikes(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> communityList = mypageDao.getMyCommunityLikes(userIdx, startIdx, size);
        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityHearts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetCommunityActivityRes> communityList = mypageDao.getMyCommunityHearts(userIdx, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageDao.getMyGroupPurchasePosts(userIdx, startIdx, size);
        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageDao.getMyGroupPurchaseLikes(userIdx, startIdx, size);
        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx, int startIdx, int size) throws BaseException{
        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageDao.getMyGroupPurchaseParticipated(userIdx, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }
}
