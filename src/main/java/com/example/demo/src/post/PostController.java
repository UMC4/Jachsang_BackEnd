package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.model.community.GetCommunityPostRes;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.post.model.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.model.recipe.GetRecipePostRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/app/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    public PostController(PostProvider postProvider, PostService postService) {
        this.postService = postService;
        this.postProvider = postProvider;
    }
    //글쓰기
    @ResponseBody
    @PostMapping(value = "create")
    public BaseResponse<PostingRes> createPost(@RequestBody Object postingReq){
        try{
            HashMap<String,Object> req = (LinkedHashMap)postingReq;
           // if(this.postProvider._isExistPostIDx((int)req.get("postIdx")) == -1) throw new BaseException();
            int categoryIdx = CATEGORY.getNumber((String)req.get("category"));
            PostingRes postingRes = this.postService.posting(categoryIdx/10, categoryIdx, req);
            return new BaseResponse<>(postingRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping(value ="get")
    public BaseResponse<Object> getPost(@RequestBody GetPostReq getPostReq){
        try{


            int boardIdx = 10*this.postProvider._getBoardIdxOf(getPostReq.getPostIdx());
            if(boardIdx == 10){
                Object result = (GetCommunityPostRes)this.postProvider.getPost(boardIdx,getPostReq);
                return new BaseResponse<>(result);
            }
            else if (boardIdx == 20){
                Object result = (GetGroupPurchasePostRes)this.postProvider.getPost(boardIdx,getPostReq);
                return new BaseResponse<>(result);
            }
            else if (boardIdx == 30){
                Object result = (GetRecipePostRes)this.postProvider.getPost(boardIdx,getPostReq);
                return new BaseResponse<>(result);
            }
            else return null; //TODO : 이 부분 예외처리하기
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping(value = "delete")
    public BaseResponse<String> deletePost(@RequestBody DeleteReq deleteReq){
        try{
            if(this.postService.deletePost(deleteReq)) return new BaseResponse<>("성공했습니다.");
            // 실패한 경우 예외처리
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }

    @ResponseBody
    @PutMapping(value = "update")
    public BaseResponse<String> updatePost(@RequestBody Object updateReq){
        try{
            HashMap<String,Object> req = (LinkedHashMap)updateReq;
            // if(this.postProvider._isExistPostIDx((int)req.get("postIdx")) == -1) throw new BaseException();
            if(this.postService.updatePost(req))
                return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패입니다!");
    }

    @ResponseBody
    @PostMapping(value = "scrap")
    public BaseResponse<String> scrapPost(@RequestBody LikeReq likeReq){
        try{
            if(this.postService.scrapPost(likeReq)) return new BaseResponse<>("성공했습니다.");
            // 실패한 경우 예외처리
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }
    @ResponseBody
    @PostMapping(value = "scrap/cancel")
    public BaseResponse<String> cancelScrapPost(@RequestBody LikeReq likeReq){
        try{
            if(this.postService.cancelScrapPost(likeReq)) return new BaseResponse<>("성공했습니다.");
            // 실패한 경우 예외처리
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }
    //TODO : 게시글 하트 취소 API 작성해야함.
    @ResponseBody
    @PostMapping(value = "heart")
    public BaseResponse<String> heartPost(@RequestBody HeartPostReq heartPostReq){
        try{
            if(this.postService.heartPost(heartPostReq)) return new BaseResponse<>("성공했습니다.");
            // 실패한 경우 예외처리
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }

    @ResponseBody
    @PostMapping(value = "heart/cancel")
    public BaseResponse<String> cancelHeartPost(@RequestBody HeartPostReq heartPostReq){
        try{
            if(this.postService.cancelHeartPost(heartPostReq)) return new BaseResponse<>("성공했습니다.");
            // 실패한 경우 예외처리
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }

    @ResponseBody
    @GetMapping(value = "get/like")
    public BaseResponse<Integer> getLikeCount(@RequestParam("postIdx") int postIdx){
        try{
            int likeCount = this.postProvider.getLikeCount(postIdx);
            //if(likeCount == -1) TODO:예외처리하기
            return new BaseResponse<>(likeCount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}
