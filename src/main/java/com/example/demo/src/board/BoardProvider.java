package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.REQUEST_ERROR;

@Service
public class BoardProvider {

    private final BoardDao boardDao;

    @Autowired
    public BoardProvider(BoardDao boardDao) { this.boardDao = boardDao; }

    // 커뮤니티 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getFilteredCommunityList(int userIdx, int categoryIdx, int limit) {
        List<GetCommunityItemRes> getCommunityListRes = boardDao.filterCommunityByCategory(userIdx, categoryIdx, limit);
        return getCommunityListRes;
    }

    // 커뮤니티 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetCommunityItemRes> getSortedCommunityList(int userIdx, SortType sortType, int limit) {
        List<GetCommunityItemRes> getSortedCommunityList = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            getSortedCommunityList = boardDao.sortCommunityByLatest(userIdx, limit);
        }
        //정렬 조건이 "인기순"인 경우
        else if(sortType == SortType.POPULAR) {
            getSortedCommunityList = boardDao.sortCommunityByPopularity(userIdx, limit);
        }
        return getSortedCommunityList;
    }


    // 공동구매 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getFilteredGroupPurchaseList(int userIdx, int categoryIdx, int limit) {
        List<GetGroupPurchaseItemRes> getGroupPurchaseListRes = boardDao.filterGroupPurchaseByCategory(userIdx, categoryIdx, limit);
        return getGroupPurchaseListRes;
    }

    // 공동구매 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetGroupPurchaseItemRes> getSortedGroupPurchaseList(int userIdx, SortType sortType, int limit) {
        List<GetGroupPurchaseItemRes> getSortedGroupPurchaseList = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            getSortedGroupPurchaseList = boardDao.sortGroupPurchaseByLatest(userIdx, limit);
        }
        //정렬 조건이 "마감임박순"인 경우
        else if(sortType == SortType.DEADLINE) {
            getSortedGroupPurchaseList = boardDao.sortGroupPurchaseByRemainTime(userIdx, limit);
        }
        return getSortedGroupPurchaseList;
    }

    // 레시피 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public List<GetRecipeItemRes> getSortedRecipeList(int userIdx, SortType sortType, int limit) {
        List<GetRecipeItemRes> getSortedRecipeList = null;
        // 정렬 조건이 최신순인 경우
        if(sortType == SortType.LATEST) {
            getSortedRecipeList = boardDao.sortRecipeByLatest(userIdx, limit);
        }
        // 정렬 조건이 인기순인 경우
        else if(sortType == SortType.POPULAR) {
            getSortedRecipeList = boardDao.sortRecipeByPopularity(userIdx, limit);
        }
        return getSortedRecipeList;
    }

    public List<GetCommunityItemRes> getSearchedCommunityList(int userIdx, String query) {
        return boardDao.searchCommunity(userIdx, query);
    }

    public List<GetGroupPurchaseItemRes> getSearchedGroupPurchaseList(int userIdx, String query) {
        return boardDao.searchGroupPurchase(userIdx, query);
    }

    public List<GetRecipeItemRes> getSearchedRecipeList(int userIdx, String query) throws BaseException {
        String[] keywords = query.split(" ");
        // 검색어 유무 검사
        if (keywords.length == 0) {
            throw new BaseException(REQUEST_ERROR);
        }

        // 검색어가 ㅁ두
        boolean firstIsTag = keywords[0].startsWith("#");
        for (String keyword : keywords) {
            if (keyword.startsWith("#") != firstIsTag) {
                throw new BaseException(REQUEST_ERROR);
            }
        }

        return boardDao.searchRecipe(userIdx, query, firstIsTag);
    }
}
