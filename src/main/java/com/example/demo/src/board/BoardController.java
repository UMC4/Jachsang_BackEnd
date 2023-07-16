package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/app/boards")
public class BoardController {

    @Autowired
    private final BoardProvider boardProvider;
    @Autowired
    private final BoardService boardService;
    @Autowired
    private final JwtService jwtService;

    public BoardController(BoardProvider boardProvider, BoardService boardService, JwtService jwtService) {
        this.boardProvider = boardProvider;
        this.boardService = boardService;
        this.jwtService = jwtService;
    }


    /**
     * 커뮤니티 카테고리 필터링 조회 API
     * 필터링된 리스트 반환 (맛집이야기, 질문있어요, 대화해요, 공지)
     * [GET] /boards/community?category=keyword&limit=개수

     * 커뮤니티 정렬 조회 API
     * 정렬된 리스트 반환 (최신순, 인기순)
     * [GET] /boards/community?sort=keyword&limit=개수

     * @return BaseResponse<List<GetCommunityItemRes>>
     */
    // 커뮤니티 게시판
    @ResponseBody
    @GetMapping("/community")
    public BaseResponse<List<GetCommunityItemRes>> getCommunityList(@RequestParam(value = "category", required = false) String category,
                                                                    @RequestParam(value = "sort", required = false) String sort,
                                                                    @RequestParam(value = "limit", required = false, defaultValue = "2147483647") int limit) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetCommunityItemRes> communityList;

            // category와 sort 중 하나만 사용할 수 있다.
            // 둘 다 사용하거나, 둘 다 사용하지 않으면 오류를 발생한다.
            if ((category == null) == (sort == null)) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            }
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchaseList에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
                } else {
                    communityList = boardProvider.getFilteredCommunityList(userIdxByJWT, categoryIdx, limit);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                communityList = boardProvider.getSortedCommunityList(userIdxByJWT, sortType, limit);
            }

            return new BaseResponse<>(communityList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공동구매 카테고리 필터링 조회 API
     * 필터링된 리스트 반환 (식재료, 생활용품, 기타)
     * [GET] /boards/groupPurchase?category=keyword&limit=개수

     * 공동구매 정렬 조회 API
     * 정렬된 리스트 반환 (최신순, 마감기한순)
     * [GET] /boards/groupPurchase?sort=keyword&limit=개수

     * @return BaseResponse<List<GetGroupPurchaseItemRes>>
     */
    // 그룹 구매 게시판
    @ResponseBody
    @GetMapping("/grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchaseList(@RequestParam(value = "category", required = false) String category,
                                                                            @RequestParam(value = "sort", required = false) String sort,
                                                                            @RequestParam(value = "limit", required = false, defaultValue = "2147483647") int limit) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetGroupPurchaseItemRes> groupPurchaseList;

            // category와 sort 중 하나만 사용할 수 있다.
            // 둘 다 사용하거나, 둘 다 사용하지 않으면 오류를 발생한다.
            if ((category == null) == (sort == null)) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            }
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchaseList에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
                } else {
                    groupPurchaseList = boardProvider.getFilteredGroupPurchaseList(userIdxByJWT, categoryIdx, limit);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                groupPurchaseList = boardProvider.getSortedGroupPurchaseList(userIdxByJWT, sortType, limit);
            }

            return new BaseResponse<>(groupPurchaseList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 레시피 정렬 조회 API
     * 정렬된 리스트 반환 (최신순, 인기순)
     * [GET] /boards/recipe?sort=keyword&limit=개수
     *
     * @return BaseResponse<List<GetRecipeItemRes>>
     */
    // 레시피 게시판
    @ResponseBody
    @GetMapping("/recipe")
    public BaseResponse<List<GetRecipeItemRes>> getRecipeList(@RequestParam(value = "sort") String sort,
                                                              @RequestParam(value = "limit", required = false, defaultValue = "2147483647") int limit) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetRecipeItemRes> recipeList;
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            if(sort == null || sort.equals("마감임박순")) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            } else {
                SortType sortType = SortType.fromName(sort);
                recipeList = boardProvider.getSortedRecipeList(userIdxByJWT, sortType, limit);
            }

            return new BaseResponse<>(recipeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 커뮤니티 검색 API
     * [GET] /boards/community/search?query=keyword
     *
     * @return BaseResponse<List<GetCommunityItemRes>>
     */
    // 커뮤니티 게시판 검색
    @ResponseBody
    @GetMapping("/community/search")
    public BaseResponse<List<GetCommunityItemRes>> searchCommunityList(@RequestParam("query") String query) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetCommunityItemRes> communityList;
            //쿼리 값이 없으면 오류 발생시킨다.
            if(query == null) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            } else {
                communityList = boardProvider.getSearchedCommunityList(userIdxByJWT, query);
            }

            return new BaseResponse<>(communityList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공동구매 검색 API
     * [GET] /boards/grouppurchase/search?query=keyword
     *
     * @return BaseResponse<List<GetGroupPurchaseItemRes>>
     */
    // 공동구매 게시판 검색
    @ResponseBody
    @GetMapping("/grouppurchase/search")
    public BaseResponse<List<GetGroupPurchaseItemRes>> searchGroupPurchaseList(@RequestParam("query") String query) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetGroupPurchaseItemRes> groupPurchaseList;

            //쿼리 값이 없으면 오류 발생시킨다.
            if(query == null) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            } else {
                groupPurchaseList = boardProvider.getSearchedGroupPurchaseList(userIdxByJWT, query);
            }

            return new BaseResponse<>(groupPurchaseList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 레시피 검색 API
     * [GET] /boards/recipe/search?query=keyword
     *
     * @return BaseResponse<List<GetRecipeItemRes>>
     */
    // 레시피 게시판 검색
    @ResponseBody
    @GetMapping("/recipe/search")
    public BaseResponse<List<GetRecipeItemRes>> searchRecipeList(@RequestParam("query") String query) {
        try {
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            //int userIdxByJWT = 1; //일반 사용자
            int userIdxByJWT = 2; //관리자

            List<GetRecipeItemRes> recipeList;

            //쿼리 값이 없으면 오류 발생시킨다.
            if(query == null) {
                throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
            } else {
                recipeList = boardProvider.getSearchedRecipeList(userIdxByJWT, query);
            }

            return new BaseResponse<>(recipeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
