package com.example.demo.src.category;

public enum CATEGORY {
    //커뮤니티(1) : 맛집이야기(1), 질문있어요(2), 대화해요(3), 공유해요(4)
    COMMUNITY(10,"커뮤니티"),
    HOT_PLACE(11,"맛집이야기"),
    QUESTION(12,"질문있어요"),
    TALKING(13,"대화해요"),
    SHARING(14,"공유해요"),
    NOTICE(15, "공지"),
    //공동구매(2) : 식재료(1), 생활용품(2), 기타(3)
    GROUP_PURCHASE(20,"공동구매"),
    FOODS(21,"식재료"),
    HOUSEHOLDS(22,"생활용품"),
    ETC(23,"기타"),
    //레시피(3)
    RECIPE(30,"레시피"),
    //댓글
    COMMENT(40,"댓글"),
    CHAT(50,"채팅");
    private final int number;
    private final String name;

    CATEGORY(int number, String name) {
        this.number = number;
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public static String getName(int number){
        for(CATEGORY c : CATEGORY.values()){
            if(c.number == number) return c.name;
        }
        return null;
    }
    public static int getNumber(String name) {
        for(CATEGORY c : CATEGORY.values()) {
            if(c.name.equals(name)) return c.number;
        }
        return 0;
    }

    public static boolean isExistCategory(int number){
        for(CATEGORY c : CATEGORY.values()){
            if (c.number == number) return true;
        }
        return false;
    }
}
