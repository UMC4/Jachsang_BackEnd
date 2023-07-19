package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.PostService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/comment")
public class CommentController {

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentProvider commentProvider, CommentService commentService){
        this.commentProvider = commentProvider;
        this.commentService = commentService;
    }
    @ResponseBody
    @PostMapping(value = "/create")
    public BaseResponse<Integer> commenting(@RequestBody CommentingReq commentingReq) {
        try {
            int result = this.commentService.commenting(commentingReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping(value = "/update")
    public BaseResponse<Integer> editComment(@RequestBody EditCommentReq editCommentReq) {
        try {
            int result = this.commentService.editComment(editCommentReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PostMapping(value = "/like")
    public BaseResponse<Integer> likeComment(@RequestBody LikeReq likeReq) {
        try {
            int result = this.commentService.likeComment(likeReq);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PostMapping(value = "/like/cancel")
    public BaseResponse<Integer> cancelLikeComment(@RequestBody LikeReq likeReq) {
        try {
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
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping(value = "/create/reply")
    public BaseResponse<Integer> replyComment(@RequestBody ReplyReq replyReq){
        try {
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
            int result = this.commentService.deleteComment(commentIdx);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
