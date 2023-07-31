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
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetUserChatRes> getUserChatList(int userIdx) throws BaseException{
        try{
            List<GetUserChatRes> getUserChatRes = userDao.getUserChatRes(userIdx);
            return getUserChatRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetFollowRes> getFollowResList(int userIdx) throws BaseException{
        try {
            List<GetFollowRes> getFollowRes=userDao.getFollowRes(userIdx);
            return getFollowRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkFollowed(PostFollowReq postFollowReq) throws BaseException{
        try{
            return userDao.checkFollow(postFollowReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkId(String id) throws BaseException{
        try {
            return userDao.checkID(id);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
       //아이디 검증 여부(아이디 다를시 exception 처리)
        try{
            UserForPassword user = userDao.getPwd(postLoginReq);
        }catch (Exception exception)
        {
            throw new BaseException(FAILED_TO_LOGIN);
        }
        //postLoginReq 정보 불러오기 및 비밀번호 검증
        UserForPassword user = userDao.getPwd(postLoginReq);
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
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

}
