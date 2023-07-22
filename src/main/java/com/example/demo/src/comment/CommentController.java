package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.PostService;
import com.example.demo.utils.JwtService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@Controller
@RequestMapping("/app/comment")
public class CommentController {

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;
    private JwtService jwtService;
    @Autowired
    public CommentController(CommentProvider commentProvider, CommentService commentService){
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = new JwtService();
    }
    @ResponseBody
    @PostMapping(value = "/create")
    public BaseResponse<Integer> commenting(@RequestBody CommentingReq commentingReq) {
        try {
            int postIdx = commentingReq.getPostIdx();
            int userIdx = jwtService.getUserIdx();
            // 존재하는 게시글인지 (3000)
            if(!this.commentService._isExistPostIdx(postIdx)) return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_POST_IDX);
            // 요청 보내는 유저 정보가 올바른지
            if(commentingReq.getUserIdx() != userIdx) return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            int result = this.commentService.commenting(commentingReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
            // 매개변수가 부족할 때 예외처리
        } catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(BaseResponseStatus.OMITTED_PARAMETER);
        }
    }

    @ResponseBody
    @PatchMapping(value = "/update")
    public BaseResponse<Integer> editComment(@RequestBody EditCommentReq editCommentReq) {
        try {
            // 유저가 다르다
            if(this.commentService._getUserIdxByCommentIdx((editCommentReq.getCommentIdx())) != jwtService.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.commentService._isExistCommentIdx(editCommentReq.getCommentIdx())) {
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.editComment(editCommentReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
            // 파라미터 누락 시
        } catch (SQLIntegrityConstraintViolationException e) {
            return new BaseResponse<>(BaseResponseStatus.OMITTED_PARAMETER);
        }
    }
    @ResponseBody
    @PostMapping(value = "/like")
    public BaseResponse<Integer> likeComment(@RequestBody LikeReq likeReq) {
        try {
            if(jwtService.getUserIdx() != likeReq.getUserIdx()) {
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.commentService._isExistCommentIdx(likeReq.getCommentIdx())){
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.likeComment(likeReq);

            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PostMapping(value = "/like/cancel")
    public BaseResponse<Integer> cancelLikeComment(@RequestBody LikeReq likeReq) {
        try {
            if(this.commentService._getUserIdxByCommentIdx((likeReq.getCommentIdx())) != jwtService.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.commentService._isExistCommentIdx(likeReq.getCommentIdx())) {
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.cancelLikeComment(likeReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping(value = "/get")
    public BaseResponse<Comment> getComment(@RequestParam("commentIdx") int commentIdx){
        try {
            Comment result = this.commentProvider.getComment(commentIdx);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping(value = "/create/reply")
    public BaseResponse<Integer> replyComment(@RequestBody ReplyReq replyReq){
        try {
            if(replyReq.getUserIdx() != jwtService.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.commentService._isExistCommentIdx(replyReq.getOriginIdx())) {
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.replying(replyReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping(value = "/delete")
    public BaseResponse<Integer> deleteComment(@RequestParam("commentIdx") int commentIdx){
        try {
            if(this.commentService._getUserIdxByCommentIdx(commentIdx) != jwtService.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.commentService._isExistCommentIdx(commentIdx)) {
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.deleteComment(commentIdx);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
