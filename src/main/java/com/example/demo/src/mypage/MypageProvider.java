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

    public List<GetCommunityActivityRes> getMyCommunityPosts(int userIdx) throws BaseException{
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityPosts(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getCommunityActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getCommunityActivityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityComments(int userIdx) throws BaseException{
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityComments(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getCommunityActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getCommunityActivityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityLikes(int userIdx) throws BaseException{
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityLikes(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getCommunityActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getCommunityActivityList;
        }
    }

    public List<GetCommunityActivityRes> getMyCommunityHearts(int userIdx) throws BaseException{
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityHearts(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getCommunityActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getCommunityActivityList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx) throws BaseException{
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchasePosts(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getGroupPurchaseActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getGroupPurchaseActivityList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx) throws BaseException{
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchaseLikes(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getGroupPurchaseActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getGroupPurchaseActivityList;
        }
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx) throws BaseException{
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchaseParticipated(userIdx);
        // 조회 결과가 없는 경우 예외 처리
        if (getGroupPurchaseActivityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getGroupPurchaseActivityList;
        }
    }
}
