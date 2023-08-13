package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.utils.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/boards")
public class BoardController {

    private final BoardProvider boardProvider;
    private final JwtService jwtService;

    public BoardController(BoardProvider boardProvider, JwtService jwtService) {
        this.boardProvider = boardProvider;
        this.jwtService = jwtService;
    }

    /**
     * 커뮤니티 카테고리 필터링 조회 API
     * 필터링된 리스트 반환 (맛집이야기, 질문있어요, 대화해요, 공지)
     * [GET] /boards/community?category=keyword&limit=개수

     * 커뮤니티 정렬 조회 API
     * 정렬된 리스트 반환 (최신순, 인기순)
     * [GET] /boards/community?sort=keyword&limit=개수

     * @return BaseResponse<GetPageRes<GetCommunityItemRes>>
     */
    // 커뮤니티 게시판
    @ResponseBody
    @GetMapping("/community")
    public BaseResponse<List<GetCommunityItemRes>> getCommunityPage(@RequestParam(value = "category", required = false) String category,
                                                                    @RequestParam(value = "sort", required = false) String sort,
                                                                    @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            List<GetCommunityItemRes> communityList;

            // category와 sort 중 하나만 사용할 수 있다.
            // 둘 다 사용하거나, 둘 다 사용하지 않으면 오류를 발생한다.
            if (category == null && sort == null) {
                throw new BaseException(BaseResponseStatus.BOTH_CATEGORY_SORT_INPUT);
            } else if (category != null && sort != null) {
                throw new BaseException(BaseResponseStatus.NO_CATEGORY_SORT_INPUT);
            }
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchasePage에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.NOT_EXIST_CATEGORY);
                } else {
                    communityList = boardProvider.getFilteredCommunityPage(userIdxByJWT, categoryIdx, startIdx, size);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchasePage에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                communityList = boardProvider.getSortedCommunityPage(userIdxByJWT, sortType, startIdx, size);
            }

            return new BaseResponse<>(communityList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 공동구매 카테고리 필터링 조회 API
     * 필터링된 리스트 반환 (식재료, 생활용품, 기타)
     * [GET] /boards/groupPurchase?category=keyword&limit=개수

     * 공동구매 정렬 조회 API
     * 정렬된 리스트 반환 (최신순, 마감임박순)
     * [GET] /boards/groupPurchase?sort=keyword&limit=개수

     * @return BaseResponse<GetPageRes<GetGroupPurchaseItemRes>>
     */
    // 그룹 구매 게시판
    @ResponseBody
    @GetMapping("/grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchasePage(@RequestParam(value = "category", required = false) String category,
                                                                            @RequestParam(value = "sort", required = false) String sort,
                                                                            @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();
            List<GetGroupPurchaseItemRes> groupPurchaseList;

            // category와 sort 중 하나만 사용할 수 있다.
            // 둘 다 사용하거나, 둘 다 사용하지 않으면 오류를 발생한다.
            if (category == null && sort == null) {
                throw new BaseException(BaseResponseStatus.BOTH_CATEGORY_SORT_INPUT);
            } else if (category != null && sort != null) {
                throw new BaseException(BaseResponseStatus.NO_CATEGORY_SORT_INPUT);
            }
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchasePage에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.NOT_EXIST_CATEGORY);
                } else {
                    groupPurchaseList = boardProvider.getFilteredGroupPurchasePage(userIdxByJWT, categoryIdx, startIdx, size);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchasePage에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                groupPurchaseList = boardProvider.getSortedGroupPurchasePage(userIdxByJWT, sortType, startIdx, size);
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
     * @return BaseResponse<GetPageRes<GetRecipeItemRes>>
     */
    // 레시피 게시판
    @ResponseBody
    @GetMapping("/recipe")
    public BaseResponse<List<GetRecipeItemRes>> getRecipePage(@RequestParam(value = "sort") String sort,
                                                              @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            List<GetRecipeItemRes> recipeList;
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchasePage에 할당한다.
            SortType sortType = SortType.fromName(sort);
            recipeList = boardProvider.getSortedRecipePage(userIdxByJWT, sortType, startIdx, size);

            return new BaseResponse<>(recipeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 커뮤니티 검색 API
     * [GET] /boards/community/search?query=keyword
     *
     * @return BaseResponse<GetPageRes<GetCommunityItemRes>>
     */
    // 커뮤니티 게시판 검색
    @ResponseBody
    @GetMapping("/community/search")
    public BaseResponse<List<GetCommunityItemRes>> searchCommunityPage(@RequestParam("query") String query,
                                                                       @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);

            List<GetCommunityItemRes> communityList;
            communityList = boardProvider.getSearchedCommunityPage(userIdxByJWT, query, startIdx, size);

            return new BaseResponse<>(communityList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공동구매 검색 API
     * [GET] /boards/grouppurchase/search?query=keyword
     *
     * @return BaseResponse<GetPageRes<GetGroupPurchaseItemRes>>
     */
    // 공동구매 게시판 검색
    @ResponseBody
    @GetMapping("/grouppurchase/search")
    public BaseResponse<List<GetGroupPurchaseItemRes>> searchGroupPurchasePage(@RequestParam("query") String query,
                                                                               @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);

            List<GetGroupPurchaseItemRes> groupPurchaseList;
            groupPurchaseList = boardProvider.getSearchedGroupPurchasePage(userIdxByJWT, query, startIdx, size);

            return new BaseResponse<>(groupPurchaseList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 레시피 검색 API
     * [GET] /boards/recipe/search?query=keyword
     *
     * @return BaseResponse<GetPageRes<GetRecipeItemRes>>
     */
    // 레시피 게시판 검색
    @ResponseBody
    @GetMapping("/recipe/search")
    public BaseResponse<List<GetRecipeItemRes>> searchRecipePage(@RequestParam("query") String query,
                                                                 @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);
            // 검색어의 종류(제목/재료 검색) 분류
            boolean firstIsTag = checkFirstIsTag(query);

            List<GetRecipeItemRes> recipeList;
            recipeList = boardProvider.getSearchedRecipePage(userIdxByJWT, query, firstIsTag, startIdx, size);

            return new BaseResponse<>(recipeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 검색어의 유무나 길이에 관한 예외처리 함수
    private void validateQuery(String query) throws BaseException {
        if(query == null) {
            throw new BaseException(BaseResponseStatus.NO_SEARCH_QUERY);
        } else if (query.length() < 2) {
            throw new BaseException(BaseResponseStatus.SHORT_SEARCH_QUERY);
        }
    }

    // 검색어의 종류(제목/재료 검색) 분류 및 예외처리 함수
    private boolean checkFirstIsTag(String query) throws BaseException {
        String[] keywords = query.split(" ");
        boolean firstIsTag = keywords[0].startsWith("#");

        for (String keyword : keywords) {
            if (keyword.startsWith("#") != firstIsTag) {
                throw new BaseException(BaseResponseStatus.MIX_SEARCH_QUERY);
            }
        }

        return firstIsTag;
    }
}
