package com.example.demo.src.user;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.StringJoiner;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     *
     * @return BaseResponse<List < GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers() {
        try {
//            if(email == null){
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // Get Users
//            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(email);
//            return new BaseResponse<>(getUsersRes);
            List<GetUserRes> getUsersRes = userProvider.getUsers();
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/userIdx")
    public BaseResponse<List<GetUserIdx>> getUsersIdx(){
        try{
            List<GetUserIdx> getUserIdxes=userProvider.getUserIdxes();
            return new BaseResponse<>(getUserIdxes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 아이디 조회 API
     * [GET] /users/getId?Email=
     */

    @ResponseBody
    @GetMapping("/getId") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<GetUserIdRes> getUserId(@RequestParam String email) {
        try {
            // Get Users
            GetUserIdRes getUserIdRes = userProvider.getUsersIdByEmail(email);
            return new BaseResponse(getUserIdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 채팅 조회 API
     * [GET] /users/:userIdx/chatting
     *
     * @return BaseResponse<getUserChatRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/chatting")
    public BaseResponse<List<GetUserChatRes>> getUserChat(@PathVariable("userIdx") int userIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저가 존재하는 채팅방 index 반환
            List<GetUserChatRes> getUserChatRes = userProvider.getUserChatList(userIdx);
            return new BaseResponse<>(getUserChatRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     *
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 친구 목록 조회 API
     * [GET] /users/:userIdx/friends
     *
     * @return BaseResponse<GetFriendRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/friends")
    public BaseResponse<List<GetFollowRes>> getUserFriends(@PathVariable("userIdx") int userIdx) {
        // Get User's friends
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowRes> getFollowRes = userProvider.getFollowResList(userIdx);
            return new BaseResponse<>(getFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     *
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     *
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 친구 추가 API
     * [POST] /users/:userIdx/follow
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/follow")
    public BaseResponse<String> followUser(@PathVariable("userIdx") int userIdx, @RequestBody PostFollowReq postFollowReq) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (postFollowReq.getFollowerId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostFollowReq postFollowReqUser=new PostFollowReq(postFollowReq.getFollowerId(),userIdx);
            userService.followUser(postFollowReqUser);
            String result = "팔로우 하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 친구 삭제 API
     * [DELETE] users/:userIdx/follow
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}/follow")
    public BaseResponse<String> deleteFollowUser(@PathVariable("userIdx") int userIdx, @RequestBody PostFollowReq postFollowReq) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            System.out.println(postFollowReq.getFollowerId());
            //userIdx와 접근한 유저가 같은지 확인
            if (postFollowReq.getFollowerId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostFollowReq postFollowReqUnfollow=new PostFollowReq(postFollowReq.getFollowerId(),userIdx);
            userService.deleteFollow(postFollowReqUnfollow);
            String result = "팔로우 취소하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserInfo(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickname(), user.getPhoneNumber(), user.getPassword(), user.getEmail());
            userService.modifyUserInfo(patchUserReq);

            String result = "변경하였습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 유저 닉네임변경
     *[PATCH] /users/setNickname
     *
     * @return
     */
    @ResponseBody
    @PatchMapping("/setNickname")
    public BaseResponse<String> modifyUserNewNickname(@RequestBody User user){
        try
        {
            int userIdxByJwt = jwtService.getUserIdx();
            PatchUserNicknameReq patchUserNicknameReq=new PatchUserNicknameReq(user.getNickname());
            userService.modifyUserNewNickname(userIdxByJwt,patchUserNicknameReq);

            String result="닉네임 설정 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /** 유저 이메일변경
     *[PATCH] /users/setEmail
     *
     * @return
     */

    @ResponseBody
    @PatchMapping("/setEmail")
    public BaseResponse<String> modifyUserNewEmail(@RequestBody User user){
        try
        {
            int userIdxByJwt = jwtService.getUserIdx();
            PatchUserEmailReq patchUserEmailReq=new PatchUserEmailReq(user.getEmail());
            userService.modifyUserNewEmail(userIdxByJwt,patchUserEmailReq);

            String result="이메일 설정 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 비밀번호 변경
     * [PATCH] /users/setPwd?
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/setPwd")
    public BaseResponse<String> modifyUserNewPwd(@RequestParam(name="email") String email,@RequestBody User user){
        try
        {
            PatchUserPwdReq patchUserPwdReq=new PatchUserPwdReq(email,user.getPassword());
            userService.modifyUserNewPwd(patchUserPwdReq);

            String result="비밀번호 설정 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
