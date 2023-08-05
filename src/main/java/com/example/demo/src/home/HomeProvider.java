package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeProvider {

    private final HomeDao homeDao;

    @Autowired
    public HomeProvider(HomeDao homeDao) { this.homeDao = homeDao; }

    // 커뮤니티 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getSortedCommunityList(int userIdx) throws BaseException {
        List<GetCommunityItemRes> getSortedCommunityList = homeDao.sortCommunityByPopularity(userIdx);

        // 조회 결과가 없는 경우 예외 처리
        if (getSortedCommunityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getSortedCommunityList;
        }
    }

    // 공동구매 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getSortedGroupPurchaseList(int userIdx) throws BaseException {
        List<GetGroupPurchaseItemRes> GetGroupPurchaseList = homeDao.sortGroupPurchaseByRemainTime(userIdx);

        // 조회 결과가 없는 경우 예외 처리
        if (GetGroupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return GetGroupPurchaseList;
        }
    }

    // 레시피 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetRecipeItemRes> getSortedRecipeList(int userIdx) throws BaseException {
        List<GetRecipeItemRes> getRecipeList = homeDao.sortRecipeByPopularity(userIdx);

        // 조회 결과가 없는 경우 예외 처리
        if (getRecipeList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getRecipeList;
        }    }
}
