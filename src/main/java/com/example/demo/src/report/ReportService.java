package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.src.report.model.ChatReportReq;
import com.example.demo.src.report.model.CommunityReportReq;
import com.example.demo.src.report.model.UserReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
public class ReportService {
    @Autowired
    private ReportDao reportDao;


    @Autowired
    public ReportService(ReportDao reportDao){
        this.reportDao = reportDao;
    }

    public int reporting(CommunityReportReq communityReportReq) throws BaseException, SQLIntegrityConstraintViolationException {
        return this.reportDao.reporting(communityReportReq);
    }

    public int reporting(UserReportReq userReportReq) throws BaseException {
        return this.reportDao.reporting(userReportReq);
    }

    public int reporting(ChatReportReq chatReportReq) throws BaseException {
        return this.reportDao.reporting(chatReportReq);
    }

    public int deleteContents(CommunityReportReq communityReportReq){
        return this.reportDao.deleteContents(communityReportReq);
    }
    public Methods _getMethods(){
        return this.reportDao._getMethods();
    }


}
