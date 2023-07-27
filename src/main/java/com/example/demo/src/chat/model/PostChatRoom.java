package com.example.demo.src.chat.model;

import com.example.demo.src.chat.ChatService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PostChatRoom {
    private Long chatRoomIdx;
    private Long userIdx;
    private String title;
    private int unreads;
    private Timestamp updateTime;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public PostChatRoom(Long chatRoomIdx, Long userIdx, String title, int unreads, Timestamp updateTime) {
        this.chatRoomIdx = chatRoomIdx;
        this.userIdx = userIdx;
        this.title = title;
        this.unreads = unreads;
        this.updateTime = updateTime;
    }

    public void handlerActions(WebSocketSession session, PostChatComment postChatComment, ChatService chatService) {
        if (postChatComment.getContentType().equals("ENTER")) {
            sessions.add(session);
            postChatComment.setContents(postChatComment.getUserIdx() + "님이 입장했습니다.");
        }
        sendMessage(postChatComment, chatService);
    }


    private <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream()
                .forEach(session -> chatService.sendMessage(session, message));
    }

}