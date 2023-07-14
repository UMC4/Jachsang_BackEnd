package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.Comment;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
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
    @PatchMapping(value = "/edit")
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
    public BaseResponse<Integer> likeComment(@RequestParam("commentIdx") int commentIdx) {
        try {
            int result = this.commentService.likeComment(commentIdx);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PostMapping(value = "/like/cancel")
    public BaseResponse<Integer> cancelLikeComment(@RequestParam("commentIdx") int commentIdx) {
        try {
            int result = this.commentService.cancelLikeComment(commentIdx);
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
}
