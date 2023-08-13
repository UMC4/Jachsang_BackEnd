package com.example.demo.src.report;

import com.example.demo.src.comment.CommentDao;
import com.example.demo.src.post.PostDao;
import com.example.demo.src.category.REPORT;
import com.example.demo.src.post.model.generalModel.DeleteReq;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.src.report.model.ChatReportReq;
import com.example.demo.src.report.model.Report;
import com.example.demo.src.report.model.CommunityReportReq;
import com.example.demo.src.report.model.UserReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Calendar;

@Repository
public class ReportDao {
    private JdbcTemplate jdbcTemplate;
    private Methods methods;
    @Autowired
    public ReportDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.methods = new Methods(dataSource);
    }

    public int reporting(CommunityReportReq communityReportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx, reportCategory" +
                ", contentsKind, reportedContentsIdx,reportedUserIdx)" +
                " VALUES (?,?,?,?,?)";

        String reportCategory = REPORT.getReportContents(communityReportReq.getReportCategory());

        Object[] param = {
                communityReportReq.getReportingUserIdx(), reportCategory, communityReportReq.getContentsKind(),
                communityReportReq.getReportedContentsIdx(), communityReportReq.getReportedUserIdx()
        };
        // 신고 횟수를 하나 늘린다.
        int categoryIdx = communityReportReq.getContentsKind();
        String category = "";
        if(categoryIdx <= 30) {
            category = "Post";
        }
        else if(categoryIdx == 40) {
            category = "Comment";
        }
        String increaseReportCountSql = "UPDATE "+category+" SET reported = reported + 1 WHERE " +
                category.toLowerCase()+"Idx = "+ communityReportReq.getReportedContentsIdx();
        this.jdbcTemplate.update(increaseReportCountSql);
        this.jdbcTemplate.update(createReportSql,param);
        return restrictContents(communityReportReq);
    }
    public int reporting(UserReportReq userReportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategory, reportedContentsIdx,reportedUserIdx,contentsKind)" +
                " VALUES (?,?,?,?,?)";
        String reportCategory = REPORT.getReportContents(userReportReq.getReportCategory());
        Object[] param = {
                userReportReq.getUserIdx(), reportCategory,
                0,userReportReq.getReportedUserIdx(), 50
        };
        // 신고 횟수를 하나 늘린다.
        String increaseReportCountSql = "UPDATE User SET userReported = userReported + 1" +
                " WHERE userIdx = "+ userReportReq.getReportedUserIdx();
        this.jdbcTemplate.update(increaseReportCountSql);
        this.jdbcTemplate.update(createReportSql,param);
        return restrictUser(userReportReq.getReportedUserIdx());
    }
    public int reporting(ChatReportReq chatReportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategory,reportedContentsIdx,reportedUserIdx,contentsKind)" +
                "VALUES (?,?,?,?,?)";
        String reportCategory = REPORT.getReportContents(chatReportReq.getReportCategory());
        Object[] param = {
                chatReportReq.getUserIdx(), reportCategory,chatReportReq.getChatRoomIdx(),
                chatReportReq.getReportedUserIdx(),60
        };

        this.jdbcTemplate.update(createReportSql,param);
        return restrictChatUser(chatReportReq.getReportedUserIdx(), chatReportReq.getReportCategory());
    }
    public Report getReport(int reportIdx){
        String getReportSql = "SELECT * FROM Report WHERE reportIdx = "+reportIdx;
        return this.jdbcTemplate.queryForObject(getReportSql,(rs, rowNum) -> new Report(
                rs.getInt("reportIdx"),
                rs.getInt("reportingUserIdx"),
                rs.getInt("reportCategoryIdx"),
                rs.getInt("contentsKind"),
                rs.getInt("reportedContentsIdx"),
                rs.getInt("reportedUserIdx"),
                rs.getString("answer"),
                rs.getTimestamp("reportAt"),
                rs.getTimestamp("finishAt")
        ));
    }

    public int restrictContents(CommunityReportReq communityReportReq){
        //신고 당한 게시글/댓글/답글의 reportCount 받기
        String getReportCountSql = "";
        String kind = "";
        int categoryIdx = communityReportReq.getContentsKind();
        // 게시글
        if(categoryIdx <= 30) kind = "Post";
        // 댓글/답글
        else if (categoryIdx == 40) kind = "Comment";
        // 신고 횟수 수집 sql
        getReportCountSql = "SELECT reported FROM "+kind+" WHERE "+kind.toLowerCase()+"Idx = "+ communityReportReq.getReportedContentsIdx();
        int reportCount = this.jdbcTemplate.queryForObject(getReportCountSql,int.class);

        int restrictDate = 7;
        // 신고 누적횟수가 5회가 되는 경우 -> 컨텐츠를 삭제한다.
        if(reportCount %5 == 5) {
            if(categoryIdx == 40) new CommentDao(jdbcTemplate.getDataSource()).deleteComment(communityReportReq.getReportedContentsIdx());
            else new PostDao(jdbcTemplate.getDataSource()).deletePost(new DeleteReq(communityReportReq.getReportedUserIdx(), communityReportReq.getReportedContentsIdx()));
            if(reportCount >= 10) {
                restrictDate = 14;
            }
        }
        else {
            String addReportCountSql = "UPDATE "+kind+" SET reported = reported+1 WHERE "+kind.toLowerCase()+"Idx = "+communityReportReq.getReportedContentsIdx();
            this.jdbcTemplate.update(addReportCountSql);
        }
        Timestamp restrictTime = this.jdbcTemplate.queryForObject("SELECT NOW()",Timestamp.class);
        Calendar cal = Calendar.getInstance();
        cal.setTime(restrictTime);
        cal.add(Calendar.DATE, restrictDate);
        restrictTime.setTime(cal.getTime().getTime());

        String setRestrictTimeSql = "UPDATE User SET updateAt = now(), communityRestrictTime = "+restrictTime +" WHERE" +
                "userIdx = "+communityReportReq.getReportingUserIdx();
        return this.jdbcTemplate.update(setRestrictTimeSql);
    }
    public int restrictUser(int userIdx){
        String checkReportedSql = "SELECT userReported FROM User WHERE userIdx = "+userIdx;
        int reported = this.jdbcTemplate.queryForObject(checkReportedSql,int.class);
        String restrictUserSql = "";
        if(reported <= 30 && reported%5 != 0) return -10;
        if(reported %5 == 0) {
            int number;
            if(reported > 10) number = 2;
            else number = 1;
            int restrictDate = 7;

            Timestamp now,CORTime,CHRTime;
            Calendar communityCal = Calendar.getInstance();
            Calendar chatCal = Calendar.getInstance();

            String getCHRtimeSql = "SELECT chatRestrictTime FROM User WHERE userIdx = "+userIdx;
            String getCORtimeSql = "SELECT communityRestrictTime FROM User WHERE userIdx = "+userIdx;
            now = this.jdbcTemplate.queryForObject("SELECT NOW()",Timestamp.class);
            CORTime = this.jdbcTemplate.queryForObject(getCORtimeSql,Timestamp.class);
            CHRTime = this.jdbcTemplate.queryForObject(getCHRtimeSql,Timestamp.class);

            if(CORTime.compareTo(now) > 0)
                 communityCal.setTime(CORTime);
            else communityCal.setTime(now);

            if(CHRTime.compareTo(now) > 0)
                chatCal.setTime(CHRTime);
            else chatCal.setTime(now);

            communityCal.add(Calendar.DATE, restrictDate*number);
            chatCal.add(Calendar.DATE, restrictDate*number);

            String comRT = new Timestamp(communityCal.getTimeInMillis()).toString();
            String chatRT = new Timestamp(chatCal.getTimeInMillis()).toString();

            comRT.substring(0,comRT.indexOf("."));
            chatRT.substring(0,chatRT.indexOf("."));

            restrictUserSql = "UPDATE User SET chatRestrictTime = ?, communityRestrictTime = ? WHERE userIdx = "+userIdx;
            Object[] param = {chatRT,comRT};
            return this.jdbcTemplate.update(restrictUserSql,param);
        }
        if(reported > 30) {
            restrictUserSql = "UPDATE User SET status = -1, updateAt = now() WHERE userIdx = "+userIdx;
            return this.jdbcTemplate.update(restrictUserSql);
        }
        return 0;
    }
    public int restrictChatUser(int userIdx, int reportCategoryIdx){
        int restrictDate = 7;
        int level = 1;
        if(reportCategoryIdx <= 4) level = 1;
        else if (reportCategoryIdx == 5) level = 2;
        else if (reportCategoryIdx == 6) {
            level = 3;
        }
        String getChatRTSql = "SELECT chatRestrictTime FROM User WHERE userIdx = "+userIdx;
        Timestamp chatRT = this.jdbcTemplate.queryForObject(getChatRTSql,Timestamp.class);
        Timestamp now = this.jdbcTemplate.queryForObject("SELECT NOW()",Timestamp.class);

        Calendar cal = Calendar.getInstance();
        if(chatRT.compareTo(now) <= 0) cal.setTime(now);
        else cal.setTime(chatRT);

        if(level == 3) cal.add(Calendar.YEAR, 10);
        cal.add(Calendar.DATE, restrictDate*level);

        String chatRtime = new Timestamp(cal.getTimeInMillis()).toString();
        chatRtime = chatRtime.substring(0,chatRtime.indexOf("."));

        String restrictChatUserSql = "UPDATE User SET updateAt = now(), chatReportedL"+level+" = chatReportedL"+level+
                "+ 1, chatRestrictTime = ?"
                + " WHERE userIdx = "+userIdx;
        Object[] param = {chatRtime};
        return jdbcTemplate.update(restrictChatUserSql,param);
    }
    public Methods _getMethods(){
        return this.methods;
    }

}
