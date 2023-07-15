package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportReq {
    private int reportingUserIdx;
    private String reportingContents;
    private int reportCategoryIdx;

    private int reportedContentsIdx;
    private int reportedUserIdx;

}
