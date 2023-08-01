package com.example.demo.src.board;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final BoardDao boardDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BoardService(BoardDao boardDao) {
        this.boardDao = boardDao;
    }
}
