package com.example.demo.src.board.model;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
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

    public static <T> GetPageRes<T> handleGetPageRes(List<T> items, int size) throws BaseException {
        if (items.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NO_POSTS_FOUND);
        } else {
            boolean isLast = items.size() != size + 1;
            if (!isLast) {
                items.remove(items.size() - 1);
            }
            return new GetPageRes<>(items, isLast);
        }
    }
}


