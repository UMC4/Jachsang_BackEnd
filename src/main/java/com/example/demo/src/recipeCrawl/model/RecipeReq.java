package com.example.demo.src.recipeCrawl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeReq {
    private String thumbnail; //ok
    private String title; //ok
    private String url; // ok
    private Post post; // ok
}
