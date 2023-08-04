package com.example.demo.src.mypage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MypageService {

    private final MypageDao mypageDao;

    @Autowired
    public MypageService(MypageDao mypageDao) { this.mypageDao = mypageDao; }
}
