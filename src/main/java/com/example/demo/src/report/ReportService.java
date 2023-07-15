package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.report.model.FinishReq;
import com.example.demo.src.report.model.ReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    @Autowired
    private ReportDao reportDao;

    @Autowired
    public ReportService(ReportDao reportDao){
        this.reportDao = reportDao;
    }

    public int reporting(ReportReq reportReq) throws BaseException {
        return this.reportDao.reporting(reportReq);
    }

    public int finish(FinishReq finishReq) throws BaseException {
        return this.reportDao.finishReport(finishReq);
    }
}
