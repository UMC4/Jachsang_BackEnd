package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.GetGroupPurchaseItemRes;
import com.example.demo.src.profile.model.GetProfileRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/profiles/{userIdx}")
public class ProfileController {

    @Autowired
    private final ProfileProvider profileProvider;
    @Autowired
    private final ProfileService profileService;
    @Autowired
    private final JwtService jwtService;

    public ProfileController(ProfileProvider profileProvider, ProfileService profileService, JwtService jwtService) {
        this.profileProvider = profileProvider;
        this.profileService = profileService;
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
            //실전용
            //int userIdxByJWT = jwtService.getUserIdx();
            //테스트용
            int userIdxByJWT = 3; //일반 사용자
            //int userIdxByJWT = 2; //관리자

            GetProfileRes getProfileRes = profileProvider.getProfile(userIdxByJWT, profileUserIdx);
            return new BaseResponse<>(getProfileRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 사용자 공동구매글 조회 API
     * 필터링된 리스트 반환 (맛집이야기, 질문있어요, 대화해요, 공지)
     * [GET] /profiles/{userIdx}/grouppurchase
     */
    @ResponseBody
    @GetMapping("grouppurchase")
    public BaseResponse<List<GetGroupPurchaseItemRes>> getGroupPurchaseList(@PathVariable("userIdx") int profileUserIdx) {
        //실전용
        //int userIdxByJWT = jwtService.getUserIdx();
        //테스트용
        int userIdxByJWT = 3; //일반 사용자
        //int userIdxByJWT = 2; //관리자

        List<GetGroupPurchaseItemRes> getGroupPurchaseList = profileProvider.getGroupPurchaseList(userIdxByJWT, profileUserIdx);
        return new BaseResponse<>(getGroupPurchaseList);
    }
}