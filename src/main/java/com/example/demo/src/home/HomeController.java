package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.GetCommunityItemRes;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.board.model.GetRecipeItemRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/app/home")
public class HomeController {

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final JwtService jwtService;

    public HomeController(HomeProvider homeProvider, JwtService jwtService) {
        this.homeProvider = homeProvider;
        this.jwtService = jwtService;
    }

    /**
     * 커뮤니티 인기글 조회 API
     * 정렬된 리스트 반환 (인기순)
     * [GET] /app/home/community

     * @return BaseResponse<List<GetCommunityItemRes>>
     */
    // 홈 인기 커뮤니티 게시글
    @ResponseBody
    @GetMapping("/community")
    public BaseResponse<List<GetCommunityItemRes>> getCommunityList() {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            return new BaseResponse<>(homeProvider.getSortedCommunityList(userIdxByJWT));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 공동구매 마감임박글 조회 API
     * 정렬된 리스트 반환 (마감임박순)
     * [GET] /app/home/grouppurchase
     * @return BaseResponse<List<GetGroupPurchaseItemRes>>
     */
    // 그룹 구매 게시판
    @ResponseBody
    @GetMapping("/grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchaseList() {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            return new BaseResponse<>(homeProvider.getSortedGroupPurchaseList(userIdxByJWT));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 레시피 인기글 조회 API
     * 정렬된 리스트 반환 (인기순)
     * [GET] /app/recipe?sort=keyword&limit=개수
     *
     * @return BaseResponse<List<GetRecipeItemRes>>
     */
    // 레시피 게시판
    @ResponseBody
    @GetMapping("/recipe")
    public BaseResponse<List<GetRecipeItemRes>> getRecipeList() {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            return new BaseResponse<>(homeProvider.getSortedRecipeList(userIdxByJWT));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
