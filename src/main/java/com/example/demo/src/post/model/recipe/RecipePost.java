package com.example.demo.src.post.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipePost {

    private int recipeDetailIdx;
    private String contents;
    private String tag;


}
