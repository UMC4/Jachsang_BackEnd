package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.*;
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

    private Long chatRoomIdx = 20L;
    private Long chatUserIdx = 20L;

    private Long chatCommentIdx = 20L;
    private final ChatDao chatDao;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }


    public PostChatRoom findRoomByChatRoomIdx(Long chatRoomIdx) {
        return chatRooms.get(chatRoomIdx);
    }


    public Long getChatRoomByPostIdx(Long postIdx) {
        return chatDao.getChatRoomByPostIdx(postIdx);
    }

    public PostChatRoom addUserToChatRoom(Long chatRoomIdx, Long userIdx, GetPost getPost) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PostChatRoom postChatRoom = PostChatRoom.builder()
                .chatRoomIdx(chatRoomIdx)
                .userIdx(userIdx)
                .title(getPost.getTitle())
                .unreads(0)
                .updateTime(timestamp)
                .build();
        chatDao.addUserToChatRoom(chatUserIdx, chatRoomIdx, userIdx);
        chatUserIdx ++;
        return postChatRoom;
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
        PostChatUser postChatUser1 = chatDao.postChatUser(chatUserIdx, getPost.getUserIdx(), chatRoomIdx);
        chatUserIdx ++;
        PostChatUser postChatUser2 = chatDao.postChatUser(chatUserIdx, getUser.getUserIdx() ,chatRoomIdx);
        chatUserIdx ++;
        chatRoomIdx ++;
        return returnPostChatRoom;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void saveChatComment(PostChatComment postChatComment) {
        chatDao.saveChatComment(postChatComment, chatCommentIdx);
        chatCommentIdx++;
    }



    public Object requestSettlement(Long chatRoomIdx, GetChatUser getChatUser) throws BaseException {
        boolean groupPurchaseCheck = chatDao.getGroupPurchaseCheck(chatRoomIdx, getChatUser);
        if (!groupPurchaseCheck) {
            int groupPurchaseMembers = chatDao.updateGroupPurchaseCheck(chatRoomIdx, getChatUser);
            chatDao.deleteChatUser(getChatUser);

            if (groupPurchaseMembers == 0)
                return 0; // 전부다 구매완료 누름
            else
                return 1; // 몇명은 구매완료 안누름
        }
        else
            return 2; // 한사람이 구매완료 2번 누름
    }

    public Object completeSettlement(Long chatRoomIdx, GetChatUser getChatUser) throws BaseException {
        int groupPurchaseMembers = chatDao.getGroupPurchaseMembers(chatRoomIdx);

        boolean hostCheck = chatDao.hostCheck(chatRoomIdx, getChatUser);

        if (groupPurchaseMembers == 0 && hostCheck) {
            return "정산이 완료되었습니다.";
        } else {
            return "정산이 완료되지 않았습니다.";
        }
    }



    public Object deleteChatRoom(Long chatRoomIdx) throws BaseException {
        int groupPurchaseMembers = chatDao.getGroupPurchaseMembers(chatRoomIdx);

        if (groupPurchaseMembers == 0) {
            return chatDao.deleteChatRoom(chatRoomIdx);
        } else {
            return "정산이 완료되지 않았습니다.";
        }
    }


}
