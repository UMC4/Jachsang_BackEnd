package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.profile.model.GetProfileRes;
import com.example.demo.utils.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("app/profiles/{userIdx}")
public class ProfileController {

    private final ProfileProvider profileProvider;
    private final JwtService jwtService;

    public ProfileController(ProfileProvider profileProvider, JwtService jwtService) {
        this.profileProvider = profileProvider;
        this.jwtService = jwtService;
    }

    /**
     * 사용자 프로필 조회 API
     * [GET] /profiles/{userIdx}/info
     */
    @ResponseBody
    @GetMapping("info")
    public BaseResponse<GetProfileRes> getProfile(@PathVariable("userIdx") int profileUserIdx) {
        try{
            int userIdxByJWT = jwtService.getUserIdx();

            GetProfileRes getProfileRes = profileProvider.getProfile(userIdxByJWT, profileUserIdx);
            return new BaseResponse<>(getProfileRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 사용자 공동구매글 조회 API
     * [GET] /profiles/{userIdx}/grouppurchase?startIdx=&size=
     */
    @ResponseBody
    @GetMapping("grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchaseList(@PathVariable("userIdx") int profileUserIdx,
                                                                                  @RequestParam(value = "startIdx", defaultValue = "0") int startIdx,
                                                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            int userIdxByJWT = jwtService.getUserIdx();

            List<GetGroupPurchaseItemRes> getGroupPurchaseList = profileProvider.getGroupPurchasePage(userIdxByJWT, profileUserIdx, startIdx, size);
            return new BaseResponse<>(getGroupPurchaseList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
