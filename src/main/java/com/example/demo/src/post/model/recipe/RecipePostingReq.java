package com.example.demo.src.post.model.recipe;

import com.example.demo.src.post.model.generalModel.PostingReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipePostingReq extends PostingReq {
    public String contents;
    public String tag;
}
