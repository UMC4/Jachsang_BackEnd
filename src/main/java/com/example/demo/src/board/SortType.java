package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;

public enum SortType {
    POPULAR("인기순"),
    LATEST("최신순"),
    DEADLINE("마감임박순");

    private final String name;

    SortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SortType fromName(String name) throws BaseException {
        for (SortType sortType : SortType.values()) {
            if (sortType.getName().equals(name)) {
                return sortType;
            }
        }
        throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
    }
}
