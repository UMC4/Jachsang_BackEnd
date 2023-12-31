package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(FAILED_GET_USERS);
        }
    }
    public List<GetUserIdx> getUserIdxes() throws BaseException{
        try {
            List<GetUserIdx> getUserIdxes=userDao.getUserIdxes();
            return getUserIdxes;
        }catch (Exception exception){
            throw new BaseException(FAILED_GET_USERIDX);
        }
    }

    public GetUserIdRes getUsersIdByEmail(String email) throws BaseException{
        try{
            GetUserIdRes getUserIdRes = userDao.getUsersByEmail(email);
            return getUserIdRes;
        }
        catch (Exception exception) {
            throw new BaseException(FAILED_GET_ID);
        }
    }
    public List<GetUserChatRes> getUserChatList(int userIdx) throws BaseException{
        try{
            List<GetUserChatRes> getUserChatRes = userDao.getUserChatRes(userIdx);
            return getUserChatRes;
        }catch (Exception exception){
            throw new BaseException(FAILED_GET_CHATROOM);
        }
    }
    public List<GetFollowRes> getFollowResList(int userIdx) throws BaseException{
        try {
            List<GetFollowRes> getFollowRes=userDao.getFollowRes(userIdx);
            return getFollowRes;
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_FOLLOW);
        }
    }
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GETUSER);
        }
    }

    //닉네임중복확인
    public int checkNickname(String nickname) throws BaseException{
        try{
            return userDao.checkNickname(nickname);
        } catch (Exception exception){
            throw new BaseException(FAILED_TO_GETMAIL);
        }
    }


    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(FAILED_TO_GETMAIL);
        }
    }

    public int checkFollowed(PostFollowReq postFollowReq) throws BaseException{
        try{
            return userDao.checkFollow(postFollowReq);
        } catch (Exception exception){
            throw new BaseException(FAILED_CHECK_FOLLOWED);
        }
    }
    public int checkId(String id) throws BaseException{
        try {
            return userDao.checkID(id);
        }catch (Exception exception){
            throw new BaseException(FAILED_CHECK_EXISTS_ID);
        }
    }
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        UserForPassword user;

        //아이디 검증 여부(아이디 다를시 exception 처리)
        try{
            user = userDao.getPwd(postLoginReq);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        //영구정지회원 검증(status가 -1일 경우 영구정지회원으로 판단함
        if(user.getStatus() == -1) {
            throw new BaseException(PERMANENT_BANNED_USER);
        }

        //postLoginReq 정보 불러오기 및 비밀번호 검증
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        } else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

}
