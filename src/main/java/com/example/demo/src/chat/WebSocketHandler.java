package com.example.demo.src.chat;

import com.example.demo.src.chat.model.PostChatComment;
import com.example.demo.src.chat.model.PostChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final Map<Long, PostChatRoom> chatRooms = new HashMap<>(); // ChatRooms 맵 생성

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);

        PostChatComment postChatComment = objectMapper.readValue(payload, PostChatComment.class);

        Long chatRoomIdx = getChatRoomIdxFromSession(session);
        if (chatRoomIdx != null) {
            PostChatRoom postChatRoom = findOrCreateChatRoom(chatRoomIdx);
            if (session.isOpen()) {
                postChatRoom.handlerActions(session, postChatComment, chatService);
                chatService.saveChatComment(postChatComment);
            }
        } else {
            log.error("No chatRoomIdx found in the session URI.");
        }
    }

    private PostChatRoom findOrCreateChatRoom(Long chatRoomIdx) {
        return chatRooms.computeIfAbsent(chatRoomIdx, key -> PostChatRoom.builder()
                .chatRoomIdx(key)
                .build());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long chatRoomIdx = getChatRoomIdxFromSession(session);
        if (chatRoomIdx != null) {
            PostChatRoom postChatRoom = findOrCreateChatRoom(chatRoomIdx);
            postChatRoom.addSession(session);
        } else {
            log.error("No chatRoomIdx found in the session URI.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long chatRoomIdx = getChatRoomIdxFromSession(session);
        if (chatRoomIdx != null) {
            PostChatRoom postChatRoom = chatRooms.get(chatRoomIdx);
            if (postChatRoom != null) {
                postChatRoom.removeSession(session);
            }
        } else {
            log.error("No chatRoomIdx found in the session URI.");
        }
    }

    private Long getChatRoomIdxFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.getPath();
            String[] parts = path.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                try {
                    return Long.parseLong(lastPart);
                } catch (NumberFormatException e) {
                    log.error("Error parsing chat room idx: {}", lastPart);
                }
            }
        }
        return null;
    }
}
