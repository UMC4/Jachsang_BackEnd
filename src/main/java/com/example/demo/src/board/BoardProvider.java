package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetPageRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.src.board.model.GetPageRes.handleGetPageRes;

@Service
public class BoardProvider {

    private final BoardDao boardDao;

    @Autowired
    public BoardProvider(BoardDao boardDao) { this.boardDao = boardDao; }


    // 커뮤니티 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public GetPageRes<GetCommunityItemRes> getFilteredCommunityPage(int userIdx, int categoryIdx, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> items = boardDao.filterCommunityByCategory(userIdx, categoryIdx, startIdx, size+1);

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    // 커뮤니티 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public GetPageRes<GetCommunityItemRes> getSortedCommunityPage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> items = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            items = boardDao.sortCommunityByLatest(userIdx, startIdx, size+1);
        }
        //정렬 조건이 "인기순"인 경우
        else if(sortType == SortType.POPULAR) {
            items = boardDao.sortCommunityByPopularity(userIdx, startIdx, size+1);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }


    // 공동구매 게시판의 목록을 카테고리로 필터링하여 가져오는 메서드입니다.
    public GetPageRes<GetGroupPurchaseItemRes> getFilteredGroupPurchasePage(int userIdx, int categoryIdx, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> items = boardDao.filterGroupPurchaseByCategory(userIdx, categoryIdx, startIdx, size+1);

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    // 공동구매 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public GetPageRes<GetGroupPurchaseItemRes> getSortedGroupPurchasePage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> items = null;
        //정렬 조건이 "최신순"인 경우
        if(sortType == SortType.LATEST) {
            items = boardDao.sortGroupPurchaseByLatest(userIdx, startIdx, size+1);
        }
        //정렬 조건이 "마감임박순"인 경우
        else if(sortType == SortType.DEADLINE) {
            items = boardDao.sortGroupPurchaseByRemainTime(userIdx, startIdx, size+1);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    // 레시피 게시판의 목록을 특정 조건으로 정렬하여 가져오는 메서드입니다.
    public GetPageRes<GetRecipeItemRes> getSortedRecipePage(int userIdx, SortType sortType, int startIdx, int size) throws BaseException {
        List<GetRecipeItemRes> items = null;
        // 정렬 조건이 최신순인 경우
        if(sortType == SortType.LATEST) {
            items = boardDao.sortRecipeByLatest(userIdx, startIdx, size+1);
        }
        // 정렬 조건이 인기순인 경우
        else if(sortType == SortType.POPULAR) {
            items = boardDao.sortRecipeByPopularity(userIdx, startIdx, size+1);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_SORT);
        }

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetCommunityItemRes> getSearchedCommunityPage(int userIdx, String query, int startIdx, int size) throws BaseException {
        List<GetCommunityItemRes> items = boardDao.searchCommunity(userIdx, query, startIdx, size+1);

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetGroupPurchaseItemRes> getSearchedGroupPurchasePage(int userIdx, String query, int startIdx, int size) throws BaseException {
        List<GetGroupPurchaseItemRes> items = boardDao.searchGroupPurchase(userIdx, query, startIdx, size+1);

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

    public GetPageRes<GetRecipeItemRes> getSearchedRecipePage(int userIdx, String query, boolean firstIsTag, int startIdx, int size) throws BaseException {
        List<GetRecipeItemRes> items = boardDao.searchRecipe(userIdx, query, firstIsTag, startIdx, size+1);

        // 조회 결과가 없는 경우 예외 처리
        return handleGetPageRes(items, size);
    }

}