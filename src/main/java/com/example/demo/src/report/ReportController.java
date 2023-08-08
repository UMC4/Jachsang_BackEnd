package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.src.report.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.example.demo.config.BaseResponseStatus.*;

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

    // 게시글/댓글 신고
    @ResponseBody
    @PostMapping(value = "/create/community")
    public BaseResponse<String> communityReporting(@RequestBody CommunityReportReq communityReportReq) {
        try {
            // 자기 자신을 신고함
            if(this.jwtService.getUserIdx() == communityReportReq.getReportedUserIdx()
            || communityReportReq.getReportingUserIdx() == communityReportReq.getReportingUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.SELF_REPORT);
            }
            // 3009 같은 대상을 여러번 신고함
            if(this.methods._isExistReport(new CheckReportReq(
                    communityReportReq.getReportingUserIdx(), communityReportReq.getContentsKind(), communityReportReq.getReportedContentsIdx()
            ))){
                return new BaseResponse<>(REPORT_COUNT_OVER);
            }
            int categoryIdx = communityReportReq.getContentsKind();
            // 게시글
            if(categoryIdx <= 30){
                if(!methods._isExistPostIdx(communityReportReq.getReportedContentsIdx()))
                    return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_POST_IDX);
            }
            // 댓글/답글
            else if (categoryIdx == 40) {
                if(!methods._isExistCommentIdx(communityReportReq.getReportedContentsIdx()))
                    return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            //카테고리가 잘못됨
            else return new BaseResponse<>(BaseResponseStatus.WRONG_CATEGORY);

            // 신고 접수 및 신고 누적 횟수 증가
            this.reportService.reporting(communityReportReq);
            return new BaseResponse<>("성공했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        //존재하지 않는 유저
        } catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(NOT_EXIST_USER);
        }
    }

    // 이용자 신고
    @ResponseBody
    @PostMapping(value = "/create/user")
    public BaseResponse<String> userReporting(@RequestBody UserReportReq userReportReq) {
        try {
            // 요청하는 유저 idx와 그의 jwt 토큰이 다름
            if(this.jwtService.getUserIdx() != userReportReq.getUserIdx()){
                return new BaseResponse<>(PERMISSION_DENIED);
            }
            // 3009 같은 대상을 여러번 신고함
            if(this.methods._isExistReport(new CheckReportReq(
                    userReportReq.getUserIdx(), 40, 0
            ))){
                return new BaseResponse<>(REPORT_COUNT_OVER);
            }
            // 신고 접수 및 신고 누적 횟수 증가
            this.reportService.reporting(userReportReq);
            return new BaseResponse<>("성공했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅방 코멘트 신고
    @ResponseBody
    @PostMapping(value = "/create/chat")
    public BaseResponse<String> chatReporting(@RequestBody ChatReportReq chatReportReq) {
        try {
            //3007 자기 자신을 신고함
            if(this.jwtService.getUserIdx() == chatReportReq.getReportedUserIdx()
            || chatReportReq.getUserIdx() == chatReportReq.getReportedUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.SELF_REPORT);
            }
            // 요청자와 요청자의 JWT가 일치하지 않음
            if(this.jwtService.getUserIdx() != jwtService.getUserIdx()){
                return new BaseResponse<>(PERMISSION_DENIED);
            }
            // 3009 같은 대상을 여러번 신고함
            if(this.methods._isExistReport(new CheckReportReq(
                    chatReportReq.getUserIdx(), 50, chatReportReq.getChatRoomIdx()
            ))){
                return new BaseResponse<>(REPORT_COUNT_OVER);
            }
            // 신고 접수 및 신고 누적 횟수 증가
            this.reportService.reporting(chatReportReq);
            return new BaseResponse<>("성공했습니다.");
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
