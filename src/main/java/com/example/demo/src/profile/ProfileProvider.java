package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.profile.model.GetProfileRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileProvider {
    private final ProfileDao profileDao;

    @Autowired
    public ProfileProvider(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }


    public GetProfileRes getProfile(int userIdx, int profileUserIdx) throws BaseException {
        GetProfileRes getProfileRes = profileDao.getProfile(userIdx, profileUserIdx);
        if (getProfileRes.getNickname() == null) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USER);
        } else {
            return getProfileRes;
        }
    }

    public List<GetGroupPurchaseItemRes> getGroupPurchaseList(int userIdx, int profileUserIdx, int limit) throws BaseException {
        List<GetGroupPurchaseItemRes> getGroupPurchaseList = profileDao.getGroupPurchaseList(userIdx, profileUserIdx, limit);
        if (getGroupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getGroupPurchaseList;
        }
    }
}
