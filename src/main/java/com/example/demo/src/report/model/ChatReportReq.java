package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatReportReq {
    private int userIdx;
    private int reportedUserIdx;
    private int chatRoomIdx;
    private int reportCategory;
}
