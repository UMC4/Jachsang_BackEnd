package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import sun.tools.jconsole.JConsole;

import java.nio.channels.ScatteringByteChannel;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

//비즈니스 로직 처리
    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        //아이디 중복
        if(userProvider.checkId(postUserReq.getLoginId())==1){
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        String pwd;
        try{
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void followUser(PostFollowReq postFollowReq) throws BaseException{
        if (userProvider.checkFollowed(postFollowReq)==1)
            throw new BaseException(FOLLOW_USER_ALREADY);
        try{
            int result=userDao.followUser(postFollowReq);
            if(result==0) {
                throw new BaseException(FOLLOW_USER_ERROR);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void deleteFollow(PostFollowReq postFollowReq) throws BaseException {
        try {
            int result = userDao.deleteFollowUser(postFollowReq);
            if (result == 0) {
                throw new BaseException(DELETE_USER_ERROR);
            }
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyUserInfo(PatchUserReq patchUserReq) throws BaseException {
        String pwd;
        try{
            pwd=new SHA256().encrypt(patchUserReq.getPassword());
            patchUserReq.setPassword(pwd);
        }catch (Exception ignored){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int result = userDao.modifyUserInfo(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
