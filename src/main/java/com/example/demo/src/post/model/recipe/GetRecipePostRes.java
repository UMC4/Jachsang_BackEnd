package com.example.demo.src.post.model.recipe;

import com.example.demo.src.post.model.generalModel.Image;
import com.example.demo.src.post.model.generalModel.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRecipePostRes extends Image {
    private int postIdx;
    private int categoryIdx;
    private int userIdx;
    private String title;
    private int viewCount;
    private int likeCount;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String url;
    private int recipeDetailIdx;
    private String contents;
    private String tag;

    public GetRecipePostRes(Post generalPost, RecipePost recipePost, List<String> paths){
        super(paths);
        this.postIdx = generalPost.getPostIdx();
        this.categoryIdx = generalPost.getCategoryIdx();
        this.userIdx = generalPost.getUserIdx();
        this.title = generalPost.getTitle();
        this.viewCount = generalPost.getViewCount();
        this.likeCount = generalPost.getLikeCount();
        this.createAt = generalPost.getCreateAt();
        this.updateAt = generalPost.getUpdateAt();
        this.url = generalPost.getUrl();
        this.recipeDetailIdx = recipePost.getRecipeDetailIdx();
        this.contents = recipePost.getContents();
        this.tag = recipePost.getTag();
    }
}
