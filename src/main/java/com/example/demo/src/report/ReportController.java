package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.report.model.FinishReq;
import com.example.demo.src.report.model.Report;
import com.example.demo.src.report.model.ReportReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/report")
public class ReportController {
    @Autowired
    private final ReportService reportService;
    @Autowired
    private final ReportProvider reportProvider;

    @Autowired
    public ReportController(ReportService reportService, ReportProvider reportProvider){
        this.reportProvider = reportProvider;
        this.reportService = reportService;
    }

    @ResponseBody
    @PostMapping(value = "/create")
    public BaseResponse<Integer> reporting(@RequestBody ReportReq reportReq) {
        try {
            int result = this.reportService.reporting(reportReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping(value = "/get")
    public BaseResponse<Report> reporting(@RequestParam("reportIdx") int reportIdx) {
        try {
            Report result = this.reportProvider.getReport(reportIdx);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PatchMapping(value = "/finish")
    public BaseResponse<Integer> finishReport(@RequestBody FinishReq finishReq) {
        try {
            int result = this.reportService.finish(finishReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
