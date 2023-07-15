package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.report.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportProvider {
    @Autowired
    private ReportDao reportDao;

    @Autowired
    public ReportProvider(ReportDao reportDao){
        this.reportDao = reportDao;
    }

    public Report getReport(int reportIdx) throws BaseException  {
        return this.reportDao.getReport(reportIdx);
    }
}
