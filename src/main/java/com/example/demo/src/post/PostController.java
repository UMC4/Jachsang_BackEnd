package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.community.GetCommunityPostRes;
import com.example.demo.src.post.generalModel.*;
import com.example.demo.src.post.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.recipe.GetRecipePostRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    @PostMapping(value = "createPost")
    public BaseResponse<PostingRes> createPost(@RequestBody Object postingReq){
        try{
            HashMap<String,Object> req = (LinkedHashMap)postingReq;
            int categoryIdx = CATEGORY.getNumber((String)req.get("category"));
            PostingRes postingRes = this.postService.posting(categoryIdx/10, categoryIdx, req);
            return new BaseResponse<>(postingRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping(value ="getPost")
    public BaseResponse<Object> getPost(@RequestBody GetPostReq getPostReq){
        try{
            if(getPostReq.getBoardName().equals("커뮤니티")){
                Object result = (GetCommunityPostRes)this.postProvider.getPost(10,getPostReq);
                return new BaseResponse<>(result);
            }
            else if (getPostReq.getBoardName().equals("공동구매")){
                Object result = (GetGroupPurchasePostRes)this.postProvider.getPost(20,getPostReq);
                return new BaseResponse<>(result);
            }
            else if (getPostReq.getBoardName().equals("레시피")){
                Object result = (GetRecipePostRes)this.postProvider.getPost(30,getPostReq);
                return new BaseResponse<>(result);
            }
            else return null; //TODO : 이 부분 예외처리하기
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping(value = "scrapPost")
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
    @PostMapping(value = "heartPost")
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
    @PostMapping(value = "deletePost")
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
    @GetMapping(value = "getLikeCount")
    public BaseResponse<Integer> getLikeCount(@RequestBody int postIdx){
        try{
            int likeCount = this.postProvider.getLikeCount(postIdx);
            //if(likeCount == -1) TODO:예외처리하기
            return new BaseResponse<>(likeCount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @PatchMapping(value = "extendDeadline")
    public BaseResponse<Timestamp> extendDeadLine(@RequestBody int posdIdx) {
        try {
            Timestamp extended = this.postService.extendDeadLine(posdIdx);
            //if(extended == null) TODO:예외처리하기
            return new BaseResponse<>(extended);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
