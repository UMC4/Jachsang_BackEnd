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
                                                                    @RequestParam(value = "limit", required = false, defaultValue = "2147483647") String limit) {
        int intLimit;
        // limit 최댓값을 초과한 경우
        try {
            intLimit = Integer.parseInt(limit);
            if (intLimit < 0) {
                return new BaseResponse<>(BaseResponseStatus.NEGATIVE_LIMIT);
            }
        } catch (NumberFormatException e) {
            // 에러 응답을 바로 반환
            return new BaseResponse<>(BaseResponseStatus.EXCESS_LIMIT);
        }

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
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchaseList에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.NOT_EXIST_CATEGORY);
                } else {
                    communityList = boardProvider.getFilteredCommunityList(userIdxByJWT, categoryIdx, intLimit);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                communityList = boardProvider.getSortedCommunityList(userIdxByJWT, sortType, intLimit);
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
     * 정렬된 리스트 반환 (최신순, 마감기한순)
     * [GET] /boards/groupPurchase?sort=keyword&limit=개수

     * @return BaseResponse<List<GetGroupPurchaseItemRes>>
     */
    // 그룹 구매 게시판
    @ResponseBody
    @GetMapping("/grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchaseList(@RequestParam(value = "category", required = false) String category,
                                                                            @RequestParam(value = "sort", required = false) String sort,
                                                                            @RequestParam(value = "limit", required = false, defaultValue = "2147483647") String limit) {
        int intLimit;
        // limit 최댓값을 초과한 경우
        try {
            intLimit = Integer.parseInt(limit);
            if (intLimit < 0) {
                return new BaseResponse<>(BaseResponseStatus.NEGATIVE_LIMIT);
            }
        } catch (NumberFormatException e) {
            // 에러 응답을 바로 반환
            return new BaseResponse<>(BaseResponseStatus.EXCESS_LIMIT);
        }

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
            // category가 입력된 경우 공동구매 리스트를 필터링해서 groupPurchaseList에 할당한다.
            else if (category != null) {
                int categoryIdx = CATEGORY.getNumber(category);
                if(categoryIdx == 0) {
                    throw new BaseException(BaseResponseStatus.NOT_EXIST_CATEGORY);
                } else {
                    groupPurchaseList = boardProvider.getFilteredGroupPurchaseList(userIdxByJWT, categoryIdx, intLimit);
                }
            }
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            else {
                SortType sortType = SortType.fromName(sort);
                groupPurchaseList = boardProvider.getSortedGroupPurchaseList(userIdxByJWT, sortType, intLimit);
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
                                                              @RequestParam(value = "limit", required = false, defaultValue = "2147483647") String limit) {
        int intLimit;
        // limit 최댓값을 초과한 경우
        try {
            intLimit = Integer.parseInt(limit);
            if (intLimit < 0) {
                return new BaseResponse<>(BaseResponseStatus.NEGATIVE_LIMIT);
            }
        } catch (NumberFormatException e) {
            // 에러 응답을 바로 반환
            return new BaseResponse<>(BaseResponseStatus.EXCESS_LIMIT);
        }

        try {
            int userIdxByJWT = jwtService.getUserIdx();

            List<GetRecipeItemRes> recipeList;
            // sort가 입력된 경우 공동구매 리스트를 정렬해서 groupPurchaseList에 할당한다.
            SortType sortType = SortType.fromName(sort);
            recipeList = boardProvider.getSortedRecipeList(userIdxByJWT, sortType, intLimit);

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
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);

            List<GetCommunityItemRes> communityList;
            communityList = boardProvider.getSearchedCommunityList(userIdxByJWT, query);

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
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);

            List<GetGroupPurchaseItemRes> groupPurchaseList;
            groupPurchaseList = boardProvider.getSearchedGroupPurchaseList(userIdxByJWT, query);

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
            int userIdxByJWT = jwtService.getUserIdx();

            // 검색어의 유무나 길이 검사
            validateQuery(query);
            // 검색어의 종류(제목/재료 검색) 분류
            boolean firstIsTag = checkFirstIsTag(query);

            List<GetRecipeItemRes> recipeList;
            recipeList = boardProvider.getSearchedRecipeList(userIdxByJWT, query, firstIsTag);

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
