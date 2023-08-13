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
    public List<GetCommunityItemRes> getFilteredCommunityPage(int userIdx, int categoryIdx, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> communityList = boardDao.filterCommunityByCategory(userIdx, categoryIdx, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    // 커뮤니티 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getSortedCommunityPage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> communityList;

        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            communityList = boardDao.sortCommunityByLatest(userIdx, startIdx, size);
        }
        //정렬 조건이 "인기순"인 경우
        else if(sortType == SortType.POPULAR) {
            communityList = boardDao.sortCommunityByPopularity(userIdx, startIdx, size);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }

    public List<GetCommunityItemRes> getSearchedCommunityPage(int userIdx, String query, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> communityList = boardDao.searchCommunity(userIdx, query, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (communityList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return communityList;
        }
    }
    
    // 공동구매 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getFilteredGroupPurchasePage(int userIdx, int categoryIdx, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> groupPurchaseList = boardDao.filterGroupPurchaseByCategory(userIdx, categoryIdx, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }

    // 공동구매 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getSortedGroupPurchasePage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> groupPurchaseList;

        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            groupPurchaseList = boardDao.sortGroupPurchaseByLatest(userIdx, startIdx, size);
        }
        //정렬 조건이 "마감임박순"인 경우
        else if(sortType == SortType.DEADLINE) {
            groupPurchaseList = boardDao.sortGroupPurchaseByRemainTime(userIdx, startIdx, size);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }
        
        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }

    public List<GetGroupPurchaseItemRes> getSearchedGroupPurchasePage(int userIdx, String query, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> groupPurchaseList = boardDao.searchGroupPurchase(userIdx, query, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (groupPurchaseList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return groupPurchaseList;
        }
    }
    
    // 레시피 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetRecipeItemRes> getSortedRecipePage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetRecipeItemRes> recipeList;

        // 정렬 조건이 최신순인 경우
        if(sortType == SortType.LATEST) {
            recipeList = boardDao.sortRecipeByLatest(userIdx, startIdx, size);
        }
        // 정렬 조건이 인기순인 경우
        else if(sortType == SortType.POPULAR) {
            recipeList = boardDao.sortRecipeByPopularity(userIdx, startIdx, size);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        if (recipeList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return recipeList;
        }
    }

    public List<GetRecipeItemRes> getSearchedRecipePage(int userIdx, String query, boolean firstIsTag, int startIdx, int size) throws BaseException {
        List<GetRecipeItemRes> recipeList = boardDao.searchRecipe(userIdx, query, firstIsTag, startIdx, size);

        // 조회 결과가 없는 경우 예외 처리
        if (recipeList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            return recipeList;
        }
    }

}