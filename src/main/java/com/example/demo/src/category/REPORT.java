package com.example.demo.src.category;

public enum REPORT {
    // 커뮤니티/채팅방 신고 (1)
    AD(111,"상업적 광고 및 판매"),
    UGLY(112,"비매너 사용자"),
    AGGRO(113,"낚시,놀람,도배"),
    BADWORDS(114,"욕설,비하"),
    SEXUAL(115,"성희롱"),
    CHEAT(116,"사칭,사기");
    private int number;
    private String contents;
    REPORT(int number, String contents){
        this.number = number;
        this.contents = contents;
    }
    static public String getReportContents(int number){
        for(REPORT r : REPORT.values()){
            if(number == r.number) return r.contents;
        }
        return "";
    }
}
