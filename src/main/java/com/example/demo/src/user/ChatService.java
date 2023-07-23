package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper;
    private Map<Long, PostChatRoom> chatRooms;

    private Long chatRoomIdx = 10L;
    private Long chatUserIdx = 10L;
    private final ChatDao chatDao;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }


    public PostChatRoom findRoomByChatRoomIdx(Long chatRoomIdx) {
        return chatRooms.get(chatRoomIdx);
    }


    public PostChatRoom postChatRoom(GetPost getPost, GetUser getUser) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PostChatRoom postChatRoom = PostChatRoom.builder()
                .chatRoomIdx(chatRoomIdx)
                .userIdx(getUser.getUserIdx())
                .title(getPost.getTitle())
                .unreads(0)
                .updateTime(timestamp)
                .build();
        chatRooms.put(chatRoomIdx, postChatRoom);
        PostChatRoom returnPostChatRoom = chatDao.postChatRoom(postChatRoom, getPost);
        PostChatUser postChatUser = chatDao.postChatUser(chatUserIdx, getUser, chatRoomIdx);
        chatRoomIdx ++;
        chatUserIdx ++;
        return returnPostChatRoom;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    public Object requestSettlement(Long chatRoomIdx, GetChatUser getChatUser) throws BaseException {
        boolean groupPurchaseCheck = chatDao.getGroupPurchaseCheck(chatRoomIdx, getChatUser);
        if (!groupPurchaseCheck) {
            int groupPurchaseMembers = chatDao.updateGroupPurchaseCheck(chatRoomIdx, getChatUser);

            if (groupPurchaseMembers == 0)
                return 0; // 전부다 공동구매 누름
            else
                return 1; // 몇명은 공동구매 안누름
        }
        else
            return 2; // 한사람이 공동구매 2번 누름
    }

    public Object deleteChatRoom(Long chatRoomIdx) throws BaseException {
        return chatDao.deleteChatRoom(chatRoomIdx);
    }


}
