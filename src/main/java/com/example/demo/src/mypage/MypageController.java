package com.example.demo.src.mypage;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mypage.model.GetCommunityActivityRes;
import com.example.demo.src.mypage.model.GetGroupPurchaseActivityRes;
import com.example.demo.utils.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/app/mypage")
public class MypageController {

    private final MypageProvider mypageProvider;
    private final JwtService jwtService;

    public MypageController(MypageProvider mypageProvider, JwtService jwtService) {
        this.mypageProvider = mypageProvider;
        this.jwtService = jwtService;
    }

    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v) throws BaseException;
    }

    private <T> BaseResponse<List<T>> getMyActivity(TriFunction<Integer, Integer, Integer, List<T>> action, int startIdx, int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            List<T> activityList = action.apply(userIdxByJWT, startIdx, size);
            return new BaseResponse<>(activityList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/community/posts")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityPosts(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyCommunityPosts, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/community/comments")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityComments(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyCommunityComments, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/community/likes")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityLikes(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyCommunityLikes, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/community/hearts")
    public BaseResponse<List<GetCommunityActivityRes>> getMyCommunityHearts(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyCommunityHearts, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/grouppurchase/posts")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchasePosts(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyGroupPurchasePosts, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/grouppurchase/likes")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchaseLikes(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyGroupPurchaseLikes, startIdx, size);
    }

    @ResponseBody
    @GetMapping("/grouppurchase/participated")
    public BaseResponse<List<GetGroupPurchaseActivityRes>> getMyGroupPurchaseParticipated(@RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return getMyActivity(mypageProvider::getMyGroupPurchaseParticipated, startIdx, size);
    }
}
