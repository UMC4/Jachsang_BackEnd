package com.example.demo.src.post.model.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteReq {
    private int userIdx;
    private int postIdx;

    public DeleteReq(){}
}
