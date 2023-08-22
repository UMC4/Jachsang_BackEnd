package com.example.demo.src.post.model.generalModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetUserDetailRes {
    private String userId;
    private String userName;
    private String userNickname;
}
