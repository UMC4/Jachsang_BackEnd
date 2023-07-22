package com.example.demo.src.report;

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
    @Autowired
    public ReportDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int reporting(ReportReq reportReq){

        String createReportSql = "INSERT INTO Report(reportingUserIdx," +
                "reportCategoryIdx, reportedContentsIdx,reportedUserIdx,reportingContents)" +
                "VALUES (?,?,?,?,?)";
        Object[] param = {
                reportReq.getReportingUserIdx(), reportReq.getReportCategoryIdx(),
                reportReq.getReportedContentsIdx(), reportReq.getReportedUserIdx(),
                reportReq.getReportingContents()
        };
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

    public int finishReport(FinishReq finishReq){
        String finishSql = "UPDATE Report SET answer = ?, finishAt = CURRENT_TIMESTAMP WHERE reportIdx = ?";
        Object[] param = {finishReq.getAnswer(), finishReq.getReportIdx()};

        return this.jdbcTemplate.update(finishSql,param);

    }


}
