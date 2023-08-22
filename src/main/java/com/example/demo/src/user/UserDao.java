package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("loginId"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("userImage"),
                        rs.getString("nickname"))
        );
    }
    public List<GetUserIdx> getUserIdxes(){
        String getUserIdxQuery="select userIdx from User";
        return this.jdbcTemplate.query(getUserIdxQuery,
                (rs, rowNum) -> new GetUserIdx(
                        rs.getInt("userIdx"))
        );
    }
    public List<GetUserChatRes> getUserChatRes(int userIdx) {
        String getUserChatQuery = "select * from ChatUser where userIdx = ?";
        int getUserChatParams = userIdx;
        return this.jdbcTemplate.query(getUserChatQuery,
                (rs, rowNum) -> new GetUserChatRes(
                        rs.getInt("chatUserIdx"),
                        rs.getInt("chatRoomIdx"),
                        rs.getInt("userIdx")),
                getUserChatParams);

    }
    public List<GetFollowRes> getFollowRes(int userIdx){
        String getUserFriendsQuery="select followingId from Follow where followerId=?";
        int getUserFriendsParams=userIdx;
        return this.jdbcTemplate.query(getUserFriendsQuery,
                (rs, rowNum) -> new GetFollowRes(
                        rs.getInt("followingId")),
                getUserFriendsParams);
    }

    public GetUserIdRes getUsersByEmail(String Email) {
        String getUsersByEmailQuery = "select loginId from User where email = ?";
        String getUsersByEmailParams = Email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserIdRes(
                        rs.getString("loginId")),
                getUsersByEmailParams);
    }
    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select * from User where userIdx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("loginId"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("userImage"),
                        rs.getString("nickname")),
                getUserParams);
    }

    public int followUser(PostFollowReq postFollowReq) {
        String followUserQuery = "insert into Follow (followerId, followingId) VALUES(?,?);";
        Object[] followUserParams = new Object[]{postFollowReq.getFollowerId(), postFollowReq.getFollowingId()};
        return this.jdbcTemplate.update(followUserQuery, followUserParams);

    }
    public int deleteFollowUser(PostFollowReq postFollowReq){
        String deleteFollowQuery="delete from Follow where followerId= ? AND followingId= ?";
        Object[] deleteFollowParams= new Object[]{postFollowReq.getFollowerId(),postFollowReq.getFollowingId()};
        return this.jdbcTemplate.update(deleteFollowQuery,deleteFollowParams);
    }
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (userName, loginId, password, email,nickname,phoneNumber,adPolicyAgreement,status) VALUES (?,?,?,?,?,?,?,1);";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getLoginId(), postUserReq.getPassword(), postUserReq.getEmail(), postUserReq.getNickname(), postUserReq.getPhoneNumber(), postUserReq.getAdPolicyAgreement()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInserIdQuery = "select last_insert_id()";  //last_insert_id 는 last_insert_id 함수는 테이블의 마지막 auto_increment 값을 리턴한다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }


    public int checkFollow(PostFollowReq postFollowReq) {
        String checkFollowQuery = "select exists(select followerId, followingId from Follow where followerId= ? AND followingId= ?)";
        Object[] checkFollowParams=new Object[]{postFollowReq.getFollowerId(),postFollowReq.getFollowingId()};
        return this.jdbcTemplate.queryForObject(checkFollowQuery,int.class,checkFollowParams);
    }


    //email 중복 체크
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    //아이디 중복 체크
    public int checkID(String id) {
        String checkIdQuery = "select exists(select loginId from User where loginId = ?)";
        String checkIdParams = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, checkIdParams);
    }
    //닉네임 중복 체크
    public int checkNickname(String nickname) {
        String checkIdQuery = "select exists(select nickname from User where nickname = ?)";
        String checkIdParams = nickname;
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, checkIdParams);
    }
    //휴대폰 전화번호 중복체크
    public int checkPhoneNumber(String phoneNumber) {
        String checkIdQuery = "select exists(select phoneNumber from User where phoneNumber = ?)";
        String checkIdParams = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, checkIdParams);
    }
    //닉네임 변경
    public int modifyUserNickname(int userIdx, PatchUserNicknameReq patchUserNicknameReq){
        String modifyUserNicknameQuery = "update User set nickname=? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserNicknameReq.getNickname(),userIdx};
        return this.jdbcTemplate.update(modifyUserNicknameQuery,modifyUserNameParams);
    }
    //휴대전화변경
    public int modifyUserPhonenumber(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set phoneNumber=?  where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickname(),patchUserReq.getPhoneNumber(),patchUserReq.getPassword(),patchUserReq.getEmail(),patchUserReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }
    //이메일변경
    public int modifyUserEmail(int userIdx,PatchUserEmailReq patchUserEmailReq){
        String modifyUserEmailQuery = "update User set email=?  where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserEmailReq.getEmail(),userIdx};
        return this.jdbcTemplate.update(modifyUserEmailQuery,modifyUserNameParams);
    }
    public int modifyUserInfo(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickname=?,phoneNumber=?,password=?,email=?  where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickname(),patchUserReq.getPhoneNumber(),patchUserReq.getPassword(),patchUserReq.getEmail(),patchUserReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public int modifyUserNewPwd(PatchUserPwdReq patchUserPwdReq){
        String modifyUserNewPwdQuery="update User set password =? where email= ?";
        Object[] modifyUserNewParams=new Object[]{patchUserPwdReq.getPassword(),patchUserPwdReq.getEmail()};
        return this.jdbcTemplate.update(modifyUserNewPwdQuery,modifyUserNewParams);
    }

    public UserForPassword getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, loginId,userName,password,email,status from User where loginId = ?";
        String getPwdParams = postLoginReq.getLoginId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new UserForPassword (
                        rs.getInt("userIdx"),
                        rs.getString("loginId"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getInt("status")
                ),
                getPwdParams
                );

    }


}
