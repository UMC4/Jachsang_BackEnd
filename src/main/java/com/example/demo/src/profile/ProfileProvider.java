package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetPageRes;
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

    public GetPageRes<GetGroupPurchaseItemRes> getGroupPurchasePage(int userIdx, int profileUserIdx, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> items = profileDao.getGroupPurchaseList(userIdx, profileUserIdx, startIdx, size+1);

        if (items.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            boolean isLast = items.size() != size + 1;
            if(!isLast) {
                items.remove(items.size() - 1);
            }

            return new GetPageRes<>(items, isLast);
        }
    }
}
