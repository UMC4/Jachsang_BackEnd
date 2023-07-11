package com.example.demo.src.category;

public enum GROUP_PURCHASE {
    //커뮤니티(1) : 최신글(1), 인기글(2), 맛집이야기(3), 질문있어요(4), 대화해요(5), 공유해요(6)
    //
    //공동구매(2) : 최신공구(1), 마감임박공구(2), 식재료(3), 생활용품(4), 기타(5)
    //
    //레시피(3) : 최신순(1), 인기순(2)
    GROUP_PURCHASE(20,"공동구매"),
    NEW_POST(21,"최신공구"),
    DEADLY(22,"마감임박공구"),
    FOODS(23,"식재료"),
    HOUSEHOLDS(24,"생활용품"),
    ETC(25,"기타");

    private final int number;
    private final String name;

    GROUP_PURCHASE(int number, String name) {
        this.number = number;
        this.name = name;
    }
}