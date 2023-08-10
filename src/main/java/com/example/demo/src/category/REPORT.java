package com.example.demo.src.category;

public enum REPORT {
    AD(1,"상업적 광고 및 판매"),
    UGLY(2,"비매너 사용자"),
    AGGRO(3,"낚시,놀람,도배"),
    BADWORDS(4,"욕설,비하"),
    SEXUAL(5,"성희롱"),
    CHEAT(6,"사칭,사기");
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
