package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.src.report.model.FinishReq;
import com.example.demo.src.report.model.Report;
import com.example.demo.src.report.model.ReportReq;
import com.example.demo.utils.JwtService;
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

    private Methods methods;
    private JwtService jwtService;
    @Autowired
    public ReportController(ReportService reportService, ReportProvider reportProvider){
        this.reportProvider = reportProvider;
        this.reportService = reportService;
        this.methods = this.reportService._getMethods();
        this.jwtService = new JwtService();
    }

    @ResponseBody
    @PostMapping(value = "/create")
    public BaseResponse<String> reporting(@RequestBody ReportReq reportReq) {
        try {
            int categoryIdx = reportReq.getReportCategoryIdx();
            // 게시글
            if(categoryIdx <= 30){
                if(!methods._isExistPostIdx(reportReq.getReportedContentsIdx()))
                    return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_POST_IDX);
            }
            // 댓글/답글
            else if (categoryIdx == 40) {
                if(!methods._isExistCommentIdx(reportReq.getReportedContentsIdx()))
                    return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            // 채팅방
            else if (categoryIdx == 50) {
                // 채팅방의 카테고리를 설정해야함
                // status에 해당 예외 추가해야함.
                // 채팅방 코멘트가 존재하지 않을 때 예외처리
                // dao에 새로운 메서드 만들어서 따로 처리해야함.
            }
            //카테고리가 잘못됨
            else return new BaseResponse<>(BaseResponseStatus.WRONG_CATEGORY);
            //3007 자기 자신을 신고함
            if(this.jwtService.getUserIdx() == reportReq.getReportingUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.SELF_REPORT);
            }
            String delete= "";
            // 신고 접수 및 신고 누적 횟수 증가
            this.reportService.reporting(reportReq);
            // 신고된 컨텐츠를 조건이 되면 삭제시키기
            if(this.reportService.deleteContents(reportReq) != 0) delete = " 신고 누적으로 인해 해당 컨텐츠가 삭제됐습니다.";
            return new BaseResponse<>("성공했습니다."+delete);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping(value = "/get")
    public BaseResponse<Report> getReport(@RequestParam("reportIdx") int reportIdx) {
        try {
            Report result = this.reportProvider.getReport(reportIdx);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


}
