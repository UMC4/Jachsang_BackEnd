package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.model.community.GetCommunityPostRes;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.post.model.groupPurchase.GetGroupPurchasePostRes;
import com.example.demo.src.post.model.recipe.GetRecipePostRes;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/app/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JwtService jwtService;
    private Methods methods;
    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    public PostController(PostProvider postProvider, PostService postService) {
        this.postService = postService;
        this.postProvider = postProvider;
        this.jwtService = new JwtService();
        this.methods = this.postService._getMethods();
    }
    //글쓰기
    ///
    @ResponseBody
    @PostMapping(value = "create")
    public BaseResponse<PostingRes> createPost(@RequestBody Object postingReq){
        try{
            HashMap<String,Object> req = (LinkedHashMap)postingReq;
            //카테고리가 존재하지 않는 것일 때
            if(CATEGORY.getNumber((String)req.get("category")) == 0){
                throw new BaseException(BaseResponseStatus.WRONG_CATEGORY);
            }
            //공지 작성 시 유저 권한이 없을 때
            if(((String)req.get("category")).equals("공지")){
                if(!this.methods._getUserRole((int)req.get("userIdx")).equals("admin")){
                    throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
                }
            }
            // postIdx가 존재하지 않을 때
           if(!this.methods._isExistPostIdx((int)req.get("postIdx"))) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);
            int categoryIdx = CATEGORY.getNumber((String)req.get("category"));

            PostingRes postingRes = this.postService.posting(categoryIdx/10, categoryIdx, req);
            if(postingRes != null) return new BaseResponse<>(postingRes);
            // 파라미터가 누락되었을 때
            else throw new SQLIntegrityConstraintViolationException();
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(BaseResponseStatus.OMITTED_PARAMETER);
        }
    }

    @ResponseBody
    @GetMapping(value ="get")
    public BaseResponse<Object> getPost(@RequestBody GetPostReq getPostReq){
        try{
            // 3000
            if(!this.methods._isExistPostIdx(getPostReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);

            int boardIdx = 10*this.methods._getBoardIdx(getPostReq.getPostIdx());
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
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }

    @ResponseBody
    @DeleteMapping(value = "delete")
    public BaseResponse<String> deletePost(@RequestBody DeleteReq deleteReq){
        try{
            // 3000
            if(!this.methods._isExistPostIdx(deleteReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);
            // 글쓴이와 삭제자가 다를 때
//            if(jwtService.getUserIdx() != this.methods._getUserIdxByPostIdx(deleteReq.getPostIdx())) {
//                // 관리자가 아니면 권한없음 예외처리
//                if(!this.methods._getUserRole(jwtService.getUserIdx()).toLowerCase().equals("admin")) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
//                throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
//            }
            if(this.postService.deletePost(deleteReq)) return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("성공했습니다..");
    }

    @ResponseBody
    @PutMapping(value = "update")
    public BaseResponse<String> updatePost(@RequestBody Object updateReq){
        try{
            HashMap<String,Object> req = (LinkedHashMap)updateReq;
            int userIdx = jwtService.getUserIdx();
            //3008
            if(userIdx != this.methods._getUserIdxByPostIdx((int)(req.get("postIdx")))){
                return new BaseResponse<>(BaseResponseStatus.PERMISSION_DENIED);
            }
            // 글자 길이 예외처리

            if(this.methods._isExistPostIdx((int)req.get("postIdx"))) {
                return new BaseResponse<>(BaseResponseStatus.NOT_EXIST_POST_IDX);
            };
            if(this.postService.updatePost(req))
                return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(BaseResponseStatus.OMITTED_PARAMETER);
        }
        return new BaseResponse<>("실패입니다!");
    }

    @ResponseBody
    @PostMapping(value = "scrap")
    public BaseResponse<String> scrapPost(@RequestBody LikeReq likeReq){
        try{
            // 요청하는 유저와 당사자가 다른 경우
            if(jwtService.getUserIdx() != likeReq.getUserIdx()) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(likeReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);
            if(this.postService.scrapPost(likeReq)) return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }
    @ResponseBody
    @PostMapping(value = "scrap/cancel")
    public BaseResponse<String> cancelScrapPost(@RequestBody LikeReq likeReq){
        try{
            if(jwtService.getUserIdx() != likeReq.getUserIdx()) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(likeReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);
            if(this.postService.cancelScrapPost(likeReq)) return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }
    @ResponseBody
    @PostMapping(value = "heart")
    public BaseResponse<String> heartPost(@RequestBody HeartPostReq heartPostReq){
        try{
            if(jwtService.getUserIdx() != heartPostReq.getUserIdx()) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(heartPostReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);

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
            if(jwtService.getUserIdx() != heartPostReq.getUserIdx()) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(heartPostReq.getPostIdx())) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);

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
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(postIdx)) throw new BaseException(BaseResponseStatus.NOT_EXIST_POST_IDX);
            int likeCount = this.postProvider.getLikeCount(postIdx);

            //if(likeCount == -1) TODO:예외처리하기
            return new BaseResponse<>(likeCount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}
