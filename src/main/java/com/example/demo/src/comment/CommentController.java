package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.PostService;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.utils.JwtService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.example.demo.config.BaseResponseStatus.JWT_USER_MISSMATCH;
import static com.example.demo.config.BaseResponseStatus.NOT_EXIST_COMMENT_IDX;

@Controller
@RequestMapping("/app/comment")
public class CommentController {

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;
    private final Methods methods;
    private JwtService jwtService;
    @Autowired
    public CommentController(CommentProvider commentProvider, CommentService commentService){
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = new JwtService();
        this.methods = commentService._getMethods();
    }
    @ResponseBody
    @PostMapping(value = "/create")
    public BaseResponse<Integer> commenting(@RequestBody CommentingReq commentingReq) {
        try {
            int postIdx = commentingReq.getPostIdx();
            int userIdx = jwtService.getUserIdx();
            // 존재하는 게시글인지 (3000)
            if(!this.methods._isExistPostIdx(postIdx)) return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_POST_IDX);
            // 요청 보내는 유저 정보가 올바른지 1018
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
            if(this.methods._getUserIdxByCommentIdx((editCommentReq.getCommentIdx())) != jwtService.getUserIdx()){
                return new BaseResponse<>(JWT_USER_MISSMATCH);
            }
            // commidx가 존재하지 않는다.
            if(!this.methods._isExistCommentIdx(editCommentReq.getCommentIdx())) {
                return new BaseResponse<>(NOT_EXIST_COMMENT_IDX);
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
            if(!this.methods._isExistCommentIdx(likeReq.getCommentIdx())){
                return new BaseResponse<>(NOT_EXIST_COMMENT_IDX);
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
            if(this.methods._getUserIdxByCommentIdx((likeReq.getCommentIdx())) != jwtService.getUserIdx()){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            if(!this.methods._isExistCommentIdx(likeReq.getCommentIdx())) {
                return new BaseResponse<>(NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.cancelLikeComment(likeReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping(value = "/get")
    public BaseResponse<Comment> getComment(@RequestParam("commentIdx") int commentIdx){
        try {
            // 존재하지 않는 cidx 3006
            if(!methods._isExistCommentIdx(commentIdx)) throw new BaseException(NOT_EXIST_COMMENT_IDX);
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
            //1018 jwt 호응 x
            if(replyReq.getUserIdx() != jwtService.getUserIdx()){
                return new BaseResponse<>(JWT_USER_MISSMATCH);
            }
            // 3006 없는 cidx
            if(!this.methods._isExistCommentIdx(replyReq.getOriginIdx())) {
                return new BaseResponse<>(NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.replying(replyReq);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping(value = "/delete")
    public BaseResponse<Integer> deleteComment(@RequestParam("commentIdx") int commentIdx){
        try {
            //1018 jwt 미호응
            if(this.methods._getUserIdxByCommentIdx(commentIdx) != jwtService.getUserIdx()){
                return new BaseResponse<>(JWT_USER_MISSMATCH);
            }
            //3006 댓글 존재하지 않음
            if(!this.methods._isExistCommentIdx(commentIdx)) {
                return new BaseResponse<>(NOT_EXIST_COMMENT_IDX);
            }
            int result = this.commentService.deleteComment(commentIdx);

            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
