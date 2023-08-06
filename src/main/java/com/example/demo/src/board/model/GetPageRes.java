package com.example.demo.src.board.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPageRes<T> {
    private List<T> items;

    @JsonProperty("isLast")
    private boolean isLast;
}
