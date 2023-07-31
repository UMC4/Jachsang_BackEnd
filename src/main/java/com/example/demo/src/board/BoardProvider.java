package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardProvider {

    private final BoardDao boardDao;

    @Autowired
    public BoardProvider(BoardDao boardDao) { this.boardDao = boardDao; }

    // 커뮤니티 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getFilteredCommunityList(int userIdx, int categoryIdx, int limit) throws BaseException {
        List<GetCommunityItemRes> getCommunityList = boardDao.filterCommunityByCategory(userIdx, categoryIdx, limit);

        // 조회 결과가 없는 경우 예외 처리
        if (getCommunityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getCommunityList;
        }
    }

    // 커뮤니티 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getSortedCommunityList(int userIdx, SortType sortType, int limit) throws BaseException {
        List<GetCommunityItemRes> getSortedCommunityList = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            getSortedCommunityList = boardDao.sortCommunityByLatest(userIdx, limit);
        }
        //정렬 조건이 "인기순"인 경우
        else if(sortType == SortType.POPULAR) {
            getSortedCommunityList = boardDao.sortCommunityByPopularity(userIdx, limit);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        if (getSortedCommunityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getSortedCommunityList;
        }
    }


    // 공동구매 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getFilteredGroupPurchaseList(int userIdx, int categoryIdx, int limit) throws BaseException {
        List<GetGroupPurchaseItemRes> getGroupPurchaseList = boardDao.filterGroupPurchaseByCategory(userIdx, categoryIdx, limit);

        // 조회 결과가 없는 경우 예외 처리
        if (getGroupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getGroupPurchaseList;
        }
    }

    // 공동구매 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getSortedGroupPurchaseList(int userIdx, SortType sortType, int limit) throws BaseException {
        List<GetGroupPurchaseItemRes> getSortedGroupPurchaseList = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            getSortedGroupPurchaseList = boardDao.sortGroupPurchaseByLatest(userIdx, limit);
        }
        //정렬 조건이 "마감임박순"인 경우
        else if(sortType == SortType.DEADLINE) {
            getSortedGroupPurchaseList = boardDao.sortGroupPurchaseByRemainTime(userIdx, limit);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        if (getSortedGroupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getSortedGroupPurchaseList;
        }
    }

    // 레시피 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetRecipeItemRes> getSortedRecipeList(int userIdx, SortType sortType, int limit) throws BaseException {
        List<GetRecipeItemRes> getSortedRecipeList = null;
        // 정렬 조건이 최신순인 경우
        if(sortType == SortType.LATEST) {
            getSortedRecipeList = boardDao.sortRecipeByLatest(userIdx, limit);
        }
        // 정렬 조건이 인기순인 경우
        else if(sortType == SortType.POPULAR) {
            getSortedRecipeList = boardDao.sortRecipeByPopularity(userIdx, limit);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        if (getSortedRecipeList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return getSortedRecipeList;
        }
    }

    public List<GetCommunityItemRes> getSearchedCommunityList(int userIdx, String query) throws BaseException {
        List<GetCommunityItemRes> communitySearchList = boardDao.searchCommunity(userIdx, query);

        // 조회 결과가 없는 경우 예외 처리
        if (communitySearchList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communitySearchList;
        }
    }

    public List<GetGroupPurchaseItemRes> getSearchedGroupPurchaseList(int userIdx, String query) throws BaseException {
        List<GetGroupPurchaseItemRes> groupPurchaseSearchList = boardDao.searchGroupPurchase(userIdx, query);

        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseSearchList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseSearchList;
        }
    }

    public List<GetRecipeItemRes> getSearchedRecipeList(int userIdx, String query, boolean firstIsTag) throws BaseException {
        List<GetRecipeItemRes> recipeSearchList = boardDao.searchRecipe(userIdx, query, firstIsTag);

        // 조회 결과가 없는 경우 예외 처리
        if (recipeSearchList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return recipeSearchList;
        }
    }

}
