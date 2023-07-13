package com.example.demo.src.post.recipe;

import com.example.demo.src.post.generalModel.Post;
import com.example.demo.src.post.generalModel.PostingReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipePostingReq extends PostingReq {
    public int recipeDetailIdx;
    public int postIdx;
    public String contents;
    public String tag;
}
