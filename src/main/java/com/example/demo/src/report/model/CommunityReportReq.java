package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityReportReq {
    private int reportCategory;
    private int reportingUserIdx;
    private int contentsKind;
    private int reportedContentsIdx;
    private int reportedUserIdx;

}
