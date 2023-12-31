package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private int reportIdx;
    private int reportingUserIdx;
    private int reportCategoryIdx;

    private int contentsKind;

    private int reportedContentsIdx;
    private int reportedUserIdx;

    private String answer;
    private Timestamp reportAt; // default current_timestamp
    private Timestamp finishAt; // default null
}
