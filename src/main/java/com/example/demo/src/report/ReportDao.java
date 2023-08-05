package com.example.demo.src.report;

import com.example.demo.src.comment.CommentDao;
import com.example.demo.src.post.PostDao;
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
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                " contentsKind, reportedContentsIdx,reportedUserIdx)" +
                "VALUES (?,?,?,?,?)";
        int kind = 0;

        Object[] param = {
                communityReportReq.getReportingUserIdx(), communityReportReq.getContentsKind(),
                communityReportReq.getReportedContentsIdx(), communityReportReq.getReportedUserIdx()
        };

        // 신고 횟수를 하나 늘린다.
        int categoryIdx = communityReportReq.getContentsKind();
        String category = "";
        if(categoryIdx <= 30) {
            category = "post";
        }
        else if(categoryIdx == 40) {
            category = "comment";
        }
        String increaseReportCountSql = "UPDATE User SET CommunityReported = CommunityReported + 1 WHERE "+category+"Idx = "+ communityReportReq.getReportedContentsIdx();
        this.jdbcTemplate.update(increaseReportCountSql);

        return this.jdbcTemplate.update(createReportSql,param);
    }

    public int reporting(UserReportReq userReportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategoryIdx, reportedContentsIdx,reportedUserIdx,reportingContents)" +
                "VALUES (?,?,?,?,?)";
        Object[] param = {
                userReportReq.getUserIdx(), 300,
                300, userReportReq.getReportedUserIdx(), "유저신고"
        };

        // 신고 횟수를 하나 늘린다.
        String increaseReportCountSql = "UPDATE User SET userReported = userReported + 1 WHERE userIdx = "+ userReportReq.getReportedUserIdx();
        this.jdbcTemplate.update(increaseReportCountSql);

        return this.jdbcTemplate.update(createReportSql,param);
    }
    public int reporting(ChatReportReq chatReportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategoryIdx,reportedUserIdx,reportingContents)" +
                "VALUES (?,?,?,?,?)";
        Object[] param = {
                chatReportReq.getUserIdx(), chatReportReq.getReportCategory(),
                chatReportReq.getReportedUserIdx()
        };

        // 신고 횟수를 하나 늘린다.
        String increaseReportCountSql = "UPDATE User SET userReported = userReported + 1 WHERE userIdx = "+
                chatReportReq.getReportedUserIdx();
        this.jdbcTemplate.update(increaseReportCountSql);

        return this.jdbcTemplate.update(createReportSql,param);
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

    public int deleteContents(CommunityReportReq communityReportReq){
        //신고 당한 게시글/댓글/답글의 reportCount 받기
        String getReportCountSql = "";
        int categoryIdx = communityReportReq.getContentsKind();
        // 게시글
        if(categoryIdx <= 30) getReportCountSql = "SELECT reported FROM Post WHERE postIdx = "+ communityReportReq.getReportedContentsIdx();
        // 댓글/답글
        else if (categoryIdx == 40) getReportCountSql = "SELECT reported FROM Comment WHERE commentIdx = "+ communityReportReq.getReportedContentsIdx();

        // 신고 횟수 수집
        int reportCount = this.jdbcTemplate.queryForObject(getReportCountSql,int.class);
        
        // 신고 누적횟수가 5회가 되는 경우 -> 컨텐츠를 삭제한다.
        if((reportCount) %5 == 0) {
            if(categoryIdx == 40) return (new CommentDao(jdbcTemplate.getDataSource()).deleteComment(communityReportReq.getReportedContentsIdx()));
            else return (new PostDao(jdbcTemplate.getDataSource()).deletePost(new DeleteReq(2, communityReportReq.getReportedContentsIdx()))) == true ? 1 : 0;
        }
        else return 0;
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
            String getCORTIME = "SELECT communityRestrictTime FROM User WHERE userIdx = "+userIdx;
            String getCARTIME = "SELECT chatRestrictTime FROM User WHERE userIdx = "+userIdx;
            Timestamp COTime = this.jdbcTemplate.queryForObject(getCORTIME,Timestamp.class);
            Timestamp CATime = this.jdbcTemplate.queryForObject(getCARTIME,Timestamp.class);
            String getCORTime = "SELECT DATE_ADD(COTime, INTERVAL "+number+" WEEK)";
            String getCARTime = "SELECT DATE_ADD(COTime, INTERVAL "+number+" WEEK)";
            restrictUserSql = "UPDATE User SET chatRestrictTime = chatRestrictTime + " +
                    getCORTime+", communityRestrictTime =  communityRestrictTime + "  +getCARTime+
                    " WHERE userIdx = "+userIdx;
        }
        if(reported > 30) {
            restrictUserSql = "UPDATE User SET status = -1 WHERE userIdx = "+userIdx;
        }
        return this.jdbcTemplate.update(restrictUserSql);
    }

    public Methods _getMethods(){
        return this.methods;
    }

}
