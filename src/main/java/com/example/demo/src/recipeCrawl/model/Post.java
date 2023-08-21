package com.example.demo.src.recipeCrawl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private String foodImage; // ok
    private String ingredients; // ok
    private String description; // ok
    // private List<String> descImage; // ok, last index is finish image; not use

}