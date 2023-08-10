package com.example.demo.src.post.model.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPostReq {
    private int categoryIdx;
    private int postIdx;
}
