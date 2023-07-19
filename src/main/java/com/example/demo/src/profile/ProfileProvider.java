package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.profile.model.GetProfileRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileProvider {
    private final ProfileDao profileDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProfileProvider(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }


    public GetProfileRes getProfile(int userIdx, int profileUserIdx) throws BaseException {
        GetProfileRes getProfileRes = profileDao.getProfile(userIdx, profileUserIdx);
        return getProfileRes;
    }

    public List<GetGroupPurchaseItemRes> getGroupPurchaseList(int userIdx, int profileUserIdx) {
        List<GetGroupPurchaseItemRes> getGroupPurchaseList = profileDao.getGroupPurchaseList(userIdx, profileUserIdx);
        return getGroupPurchaseList;
    }
}
