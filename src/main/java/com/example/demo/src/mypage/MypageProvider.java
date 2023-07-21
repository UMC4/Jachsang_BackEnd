package com.example.demo.src.mypage;

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

    public List<GetCommunityActivityRes> getMyCommunityPosts(int userIdx) {
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityPosts(userIdx);
        return getCommunityActivityList;
    }

    public List<GetCommunityActivityRes> getMyCommunityComments(int userIdx) {
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityComments(userIdx);
        return getCommunityActivityList;
    }

    public List<GetCommunityActivityRes> getMyCommunityLikes(int userIdx) {
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityLikes(userIdx);
        return getCommunityActivityList;
    }

    public List<GetCommunityActivityRes> getMyCommunityHearts(int userIdx) {
        List<GetCommunityActivityRes> getCommunityActivityList = mypageDao.getMyCommunityHearts(userIdx);
        return getCommunityActivityList;
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchasePosts(int userIdx) {
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchasePosts(userIdx);
        return getGroupPurchaseActivityList;
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseLikes(int userIdx) {
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchaseLikes(userIdx);
        return getGroupPurchaseActivityList;
    }

    public List<GetGroupPurchaseActivityRes> getMyGroupPurchaseParticipated(int userIdx, int limit) {
        List<GetGroupPurchaseActivityRes> getGroupPurchaseActivityList = mypageDao.getMyGroupPurchaseParticipated(userIdx, limit);
        return getGroupPurchaseActivityList;
    }
}
