package com.example.demo.src.category;

public enum RECIPE {
    //커뮤니티(1) : 최신글(1), 인기글(2), 맛집이야기(3), 질문있어요(4), 대화해요(5), 공유해요(6)
    //
    //공동구매(2) : 최신공구(1), 마감임박공구(2), 식재료(3), 생활용품(4), 기타(5)
    //
    //레시피(3) : 최신순(1), 인기순(2)
    GROUP_PURCHASE(30,"레시피"),
    NEW_POST(31,"최신순"),
    ETC(32,"인기순");

    private final int number;
    private final String name;

    RECIPE(int number, String name) {
        this.number = number;
        this.name = name;
    }
}
