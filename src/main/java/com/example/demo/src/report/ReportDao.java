package com.example.demo.src.report;

import com.example.demo.src.comment.CommentDao;
import com.example.demo.src.post.PostDao;
import com.example.demo.src.post.model.generalModel.DeleteReq;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.src.report.model.FinishReq;
import com.example.demo.src.report.model.Report;
import com.example.demo.src.report.model.ReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ReportDao {
    private JdbcTemplate jdbcTemplate;
    private Methods methods;
    @Autowired
    public ReportDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.methods = new Methods(dataSource);
    }

    public int reporting(ReportReq reportReq){
        // 신고 게시판에 내용 저장한다.
        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategoryIdx, reportedContentsIdx,reportedUserIdx,reportingContents)" +
                "VALUES (?,?,?,?,?)";
        Object[] param = {
                reportReq.getReportingUserIdx(), reportReq.getReportCategoryIdx(),
                reportReq.getReportedContentsIdx(), reportReq.getReportedUserIdx(),
                reportReq.getReportingContents()
        };

        // 신고 횟수를 하나 늘린다.
        int categoryIdx = reportReq.getReportCategoryIdx();
        String category = "";
        if(categoryIdx <= 30) category = "Post";
        else if(categoryIdx == 40) category = "Comment";
        else if (categoryIdx == 50) category = "ChatComment";
        String increaseReportCountSql = "UPDATE "+category+" SET reported = reported + 1 WHERE "+" = "+reportReq.getReportedContentsIdx();
        this.jdbcTemplate.update(increaseReportCountSql);

        return this.jdbcTemplate.update(createReportSql,param);
    }

    public Report getReport(int reportIdx){
        String getReportSql = "SELECT * FROM Report WHERE reportIdx = "+reportIdx;
        return this.jdbcTemplate.queryForObject(getReportSql,(rs, rowNum) -> new Report(
                rs.getInt("reportIdx"),
                rs.getInt("reportingUserIdx"),
                rs.getString("reportingContents"),
                rs.getInt("reportCategoryIdx"),
                rs.getInt("reportedContentsIdx"),
                rs.getInt("reportedUserIdx"),
                rs.getString("answer"),
                rs.getTimestamp("reportAt"),
                rs.getTimestamp("finishAt")
        ));
    }

    public int deleteContents(ReportReq reportReq){
        //신고 당한 게시글/댓글/답글의 reportCount 받기
        String getReportCountSql = "";
        int categoryIdx = reportReq.getReportCategoryIdx();
        // 게시글
        if(categoryIdx <= 30) getReportCountSql = "SELECT reported FROM Post WHERE postIdx = "+reportReq.getReportedContentsIdx();
        // 댓글/답글
        else if (categoryIdx == 40) getReportCountSql = "SELECT reported FROM Comment WHERE commentIdx = "+reportReq.getReportedContentsIdx();

        // 신고 횟수 수집
        int reportCount = this.jdbcTemplate.queryForObject(getReportCountSql,int.class);
        
        // 신고 누적횟수가 5회가 되는 경우 -> 컨텐츠를 삭제한다.
        if((reportCount+1) %5 == 0) {
            if(categoryIdx == 40) return (new CommentDao(jdbcTemplate.getDataSource()).deleteComment(reportReq.getReportedContentsIdx()));
            else return (new PostDao(jdbcTemplate.getDataSource()).deletePost(new DeleteReq(2,reportReq.getReportedContentsIdx()))) == true ? 1 : 0;
        }
        else return 0;
    }

    public Methods _getMethods(){
        return this.methods;
    }

}
