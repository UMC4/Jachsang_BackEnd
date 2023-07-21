package com.example.demo.src.mypage;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import com.example.demo.src.mypage.model.GetGroupPurchaseActivityRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/mypage")
public class MypageController {

    @Autowired
    private final MypageProvider mypageProvider;
    @Autowired
    private final MypageService mypageService;
    @Autowired
    private final JwtService jwtService;

    public MypageController(MypageProvider mypageProvider, MypageService mypageService, JwtService jwtService) {
        this.mypageProvider = mypageProvider;
        this.mypageService = mypageService;
        this.jwtService = jwtService;
    }

    /**
     * 내가 쓴 커뮤니티 글 목록 조회 API
     * [GET] /mypage/community/posts
     * @return BaseResponse<List<GetCommunityActivityRes>>
     */
    @ResponseBody
    @GetMapping("/community/posts")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityPosts() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetCommunityActivityRes> communityList = mypageProvider.getMyCommunityPosts(userIdxByJWT);
        return new BaseResponse<>(communityList);
    }

    /**
     * 내가 댓글 댓글 단 커뮤니티 글 목록 조회 API
     * [GET] /mypage/community/comments
     * @return BaseResponse<List<GetCommunityActivityRes>>
     */
    @ResponseBody
    @GetMapping("/community/comments")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityComments() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetCommunityActivityRes> communityList = mypageProvider.getMyCommunityComments(userIdxByJWT);
        return new BaseResponse<>(communityList);
    }

    /**
     * 내가 관심(like) 표시한 커뮤니티 글 목록 조회 API
     * [GET] /mypage/community/likes
     * @return BaseResponse<List<GetCommunityActivityRes>>
     */
    @ResponseBody
    @GetMapping("/community/likes")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityLikes() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 1; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetCommunityActivityRes> communityList = mypageProvider.getMyCommunityLikes(userIdxByJWT);
        return new BaseResponse<>(communityList);
    }

    /**
     * 내가 공감(heart) 표시한 커뮤니티 글 목록 조회 API
     * [GET] /mypage/community/hearts
     * @return BaseResponse<List<GetCommunityActivityRes>>
     */
    @ResponseBody
    @GetMapping("/community/hearts")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityHearts() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetCommunityActivityRes> communityList = mypageProvider.getMyCommunityHearts(userIdxByJWT);
        return new BaseResponse<>(communityList);
    }

    /**
     * 내가 쓴 공동구매 글 목록 조회 API
     * [GET] /mypage/grouppurchase/posts
     * @return BaseResponse<List<GetGroupPurchaseActivityRes>>
     */
    @ResponseBody
    @GetMapping("/grouppurchase/posts")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchasePosts() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageProvider.getMyGroupPurchasePosts(userIdxByJWT);
        return new BaseResponse<>(groupPurchaseList);
    }

    /**
     * 관심(like) 표시한 공동구매 글 목록 조회 API
     * [GET] /mypage/grouppurchase/posts
     * @return BaseResponse<List<GetGroupPurchaseActivityRes>>
     */
    @ResponseBody
    @GetMapping("/grouppurchase/likes")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchaseLikes() {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageProvider.getMyGroupPurchaseLikes(userIdxByJWT);
        return new BaseResponse<>(groupPurchaseList);
    }

    /**
     * 내가 참여한 공동구매 글 목록 조회 API
     * [GET] /mypage/grouppurchase/participated
     * @return BaseResponse<List<GetGroupPurchaseActivityRes>>
     */
    @ResponseBody
    @GetMapping("/grouppurchase/likes")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchaseParticipated(@RequestParam(value = "limit", defaultValue = "2147483647") int limit) {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetGroupPurchaseActivityRes> groupPurchaseList = mypageProvider.getMyGroupPurchaseParticipated(userIdxByJWT, limit);
        return new BaseResponse<>(groupPurchaseList);
    }
}
