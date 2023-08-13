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

    public List<GetGroupPurchaseItemRes> getGroupPurchasePage(int userIdx, int profileUserIdx, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> groupPurchaseList = profileDao.getGroupPurchaseList(userIdx, profileUserIdx, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }
}
