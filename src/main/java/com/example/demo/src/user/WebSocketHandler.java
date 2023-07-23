package com.example.demo.src.user;

import com.example.demo.src.user.model.PostChatComment;
import com.example.demo.src.user.model.PostChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);

        PostChatComment postChatComment = objectMapper.readValue(payload, PostChatComment.class);

        PostChatRoom postChatRoom = chatService.findRoomByChatRoomIdx(postChatComment.getChatRoomIdx());
        System.out.println(postChatRoom);
        System.out.println(postChatRoom.getChatRoomIdx());
        postChatRoom.handlerActions(session, postChatComment, chatService);

    }
}