package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.category.CATEGORY;
import com.example.demo.src.post.model.generalModel.*;
import com.example.demo.src.privateMethod.Methods;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.example.demo.config.BaseResponseStatus.*;

@Controller
@RequestMapping("/app/post")
public class PostController {
    private final JwtService jwtService;
    private final Methods methods;
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
    @ResponseBody
    @PostMapping(value = "create")
    public BaseResponse<PostingRes> createPost(@RequestBody Object postingReq){
        try {
            HashMap<String, Object> req = (LinkedHashMap) postingReq;

            int categoryIdx = (int) req.get("categoryIdx");

            //카테고리가 존재하지 않는 것일 때
            if (!CATEGORY.isExistCategory(categoryIdx)) {
                throw new BaseException(BaseResponseStatus.WRONG_CATEGORY);
            }
            // jwt와 userIdx가 호응하지 않음
            if (this.jwtService.getUserIdx() != (int) req.get("userIdx")) {
                //3003 공지 작성 시 유저 권한이 admin이 아닐 때
                if (categoryIdx == 15 && !methods._isAdmin(jwtService.getUserIdx())) {
                    throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
                }
            }
            PostingRes postingRes = this.postService.posting(categoryIdx, req);

            if(postingRes != null) return new BaseResponse<>(postingRes);
            // 파라미터가 누락되었을 때
            else throw new BaseException(BaseResponseStatus.OMITTED_PARAMETER);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(null);
        }
    }

    @ResponseBody
    @GetMapping(value ="get")
    public BaseResponse<Object> getPost(@RequestParam(value = "postIdx") int postIdx){
        try{
            int categoryIdx = methods._getCategoryIdx(postIdx);
            GetPostReq getPostReq = new GetPostReq(categoryIdx, postIdx);
            // 3000
            if(!this.methods._isExistPostIdx(getPostReq.getPostIdx())) throw new BaseException(NOT_EXIST_POST_IDX);

            Object result = this.postProvider.getPost(categoryIdx,postIdx);
            return new BaseResponse<>(result);

        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping(value = "delete")
    public BaseResponse<String> deletePost(@RequestParam int postIdx){
        try{
            // 3000
            if(!this.methods._isExistPostIdx(postIdx)) throw new BaseException(NOT_EXIST_POST_IDX);
            // 글쓴이와 삭제자가 다를 때
            if(jwtService.getUserIdx() != this.methods._getUserIdxByPostIdx(postIdx)) {
                // 관리자가 아니면 권한없음 예외처리
                if(!this.methods._getUserRole(jwtService.getUserIdx()).equalsIgnoreCase("admin")) throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
                throw new BaseException(JWT_USER_MISSMATCH);
            }
            if(this.postService.deletePost(postIdx)) return new BaseResponse<>("성공했습니다.");
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
            //1018 jwt 안맞아
            if(userIdx != this.methods._getUserIdxByPostIdx((int)(req.get("postIdx")))){
                throw new BaseException(JWT_USER_MISSMATCH);
            }
            // 글자 길이 예외처리
            // 존재하는 pidx인가
            if(!this.methods._isExistPostIdx((int)req.get("postIdx"))) {
                return new BaseResponse<>(NOT_EXIST_POST_IDX);
            }
            if(this.postService.updatePost(req))
                return new BaseResponse<>("성공했습니다.");
        }catch (SQLIntegrityConstraintViolationException e){
            return new BaseResponse<>(BaseResponseStatus.OMITTED_PARAMETER);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패입니다!");
    }

    @ResponseBody
    @PostMapping(value = "scrap")
    public BaseResponse<String> scrapPost(@RequestBody LikeReq likeReq){
        try{
            // 요청하는 유저와 당사자가 다른 경우
            if(jwtService.getUserIdx() != likeReq.getUserIdx()) throw new BaseException(JWT_USER_MISSMATCH);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(likeReq.getPostIdx())) throw new BaseException(NOT_EXIST_POST_IDX);
            // 자기 자신의 글에 관심을 남김
            if(jwtService.getUserIdx() == methods._getUserIdxByPostIdx(likeReq.getPostIdx())) throw new BaseException(SELF_ADDITION);
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
            // jwt와 userIdx가 호응하지 않음
            if(jwtService.getUserIdx() != likeReq.getUserIdx()) throw new BaseException(JWT_USER_MISSMATCH);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(likeReq.getPostIdx())) throw new BaseException(NOT_EXIST_POST_IDX);
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
            // jwt와 userIdx가 호응하지 않음
            if(jwtService.getUserIdx() != heartPostReq.getUserIdx()) throw new BaseException(JWT_USER_MISSMATCH);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(heartPostReq.getPostIdx())) throw new BaseException(NOT_EXIST_POST_IDX);
            // 자기 자신의 글에 좋아요를 남김
            if(jwtService.getUserIdx() == methods._getUserIdxByPostIdx(heartPostReq.getPostIdx())) throw new BaseException(SELF_ADDITION);
            // 실행문
            if(this.postService.heartPost(heartPostReq)) return new BaseResponse<>("성공했습니다.");
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("실패했습니다.");
    }

    @ResponseBody
    @PostMapping(value = "heart/cancel")
    public BaseResponse<String> cancelHeartPost(@RequestBody HeartPostReq heartPostReq){
        try{
            // jwt와 userIdx가 호응하지 않음
            if(jwtService.getUserIdx() != heartPostReq.getUserIdx()) throw new BaseException(JWT_USER_MISSMATCH);
            // 존재하지 않는 게시글인 경우
            if(!this.methods._isExistPostIdx(heartPostReq.getPostIdx())) throw new BaseException(NOT_EXIST_POST_IDX);

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
            if(!this.methods._isExistPostIdx(postIdx)) throw new BaseException(NOT_EXIST_POST_IDX);
            int likeCount = this.postProvider.getLikeCount(postIdx);
            
            return new BaseResponse<>(likeCount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}
