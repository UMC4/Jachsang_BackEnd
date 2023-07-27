package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/chat")
public class ChatController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChatProvider chatProvider;
    @Autowired
    private final ChatService chatService;


    // 체팅방 목록 - 커뮤니티
    @ResponseBody
    @GetMapping("/community")
    public BaseResponse<List<Object>> getChatRoom(@RequestBody GetUser getUser) {
        String category = "community";
        try {
            List<Object> getChatRoom = chatProvider.getChatRooms(getUser, category);
            return new BaseResponse<>(getChatRoom);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅방 목록 - 공동구매
    @ResponseBody
    @GetMapping("/grouppurchase")
    public BaseResponse<List<Object>> getChatRoom2(@RequestBody GetUser getUser) {
        String category = "grouppurchase";
        try {
            List<Object> getChatRoom = chatProvider.getChatRooms(getUser, category);
            return new BaseResponse<>(getChatRoom);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 채팅방 1개 조회 - 커뮤니티
    @ResponseBody
    @GetMapping("/community/{chatRoomIdx}")
    public BaseResponse<GetChatRoom> getChatCommentAndRoom(@PathVariable("chatRoomIdx") Long chatRoomIdx) {
        try {
            Object getChatRoom = chatProvider.getChatRoom(chatRoomIdx);
            List<GetChatComment> getChatComment = chatProvider.getChatComment(chatRoomIdx);

            GetChatRoom chatResponse = new GetChatRoom();
            chatResponse.setGetChatRoom(getChatRoom);
            chatResponse.setGetChatComment(getChatComment);

            return new BaseResponse<>(chatResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 채팅방 1개 조회 - 공동구매
    @ResponseBody
    @GetMapping("/grouppurchase/{chatRoomIdx}")
    public BaseResponse<GetChatRoom> getChatCommentAndRoom2(@PathVariable("chatRoomIdx") Long chatRoomIdx) {
        try {
            Object getChatRoom = chatProvider.getChatRoom(chatRoomIdx);
            List<GetChatComment> getChatComment = chatProvider.getChatComment(chatRoomIdx);

            GetChatRoom chatResponse = new GetChatRoom();
            chatResponse.setGetChatRoom(getChatRoom);
            chatResponse.setGetChatComment(getChatComment);

            return new BaseResponse<>(chatResponse);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 채팅방 유저 목록 조회 - 커뮤니티
    @ResponseBody
    @GetMapping("/community/{chatRoomIdx}/users")
    public BaseResponse<List<String>> getChatRoomUsers(@PathVariable("chatRoomIdx") Long chatRoomIdx) {
        try {
            List<String> getChatRoomUsers = chatProvider.getChatRoomUsers(chatRoomIdx);
            return new BaseResponse<>(getChatRoomUsers);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 채팅방 유저 목록 조회 - 공동구매
    @ResponseBody
    @GetMapping("/grouppurchase/{chatRoomIdx}/users")
    public BaseResponse<List<String>> getChatRoomUsers2(@PathVariable("chatRoomIdx") Long chatRoomIdx) {
        try {
            List<String> getChatRoomUsers = chatProvider.getChatRoomUsers(chatRoomIdx);
            return new BaseResponse<>(getChatRoomUsers);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    // 채팅방 만들기 - 커뮤니티
    @ResponseBody
    @PostMapping("/community")
    public PostChatRoom createChatRoom(@RequestBody PostChatRoomReq postChatRoomReq) {
        GetPost getPost = postChatRoomReq.getGetPost();
        GetUser getUser = postChatRoomReq.getGetUser();
        return chatService.postChatRoom(getPost, getUser);
    }

    // 채팅방 만들기 - 공동구매
    @ResponseBody
    @PostMapping("/grouppurchase")
    public PostChatRoom createChatRoom2(@RequestBody PostChatRoomReq postChatRoomReq) {
        GetPost getPost = postChatRoomReq.getGetPost();
        GetUser getUser = postChatRoomReq.getGetUser();
        return chatService.postChatRoom(getPost, getUser);
    }




    // 정산하기
    @ResponseBody
    @GetMapping("/grouppurchase/settlement")
    public BaseResponse<String> setAmount(@RequestParam(value = "chatRoomIdx") Long chatRoomIdx) {
        return new BaseResponse<>("setAmount");
    }


    // 정산하기 - N
    @ResponseBody
    @GetMapping("/grouppurchase/settlement/top-amount")
    public BaseResponse<ResponseEntity<ChatProvider.SettlementRoom>> setTopAmount(@RequestParam(value = "chatRoomIdx") Long chatRoomIdx, @RequestParam(value = "amount") int amount) {
        try {
            int members = chatProvider.getChatRoomMembers(chatRoomIdx);
            ChatProvider.SettlementRoom room = new ChatProvider.SettlementRoom(members);
            room.setTopAmount(amount);

            ResponseEntity<ChatProvider.SettlementRoom> responseEntity = ResponseEntity.ok(room);
            return new BaseResponse<>(responseEntity);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 정산하기 - x,y,z
    @ResponseBody
    @GetMapping("/grouppurchase/settlement/individual-amounts")
    public BaseResponse<ResponseEntity<ChatProvider.SettlementRoom>> setIndividualAmounts(@RequestParam(value = "chatRoomIdx") Long chatRoomIdx, @RequestBody int[] amounts) {
        try {
            int members = chatProvider.getChatRoomMembers(chatRoomIdx);
            ChatProvider.SettlementRoom room = new ChatProvider.SettlementRoom(members);
            room.setIndividualAmounts(amounts);

            ResponseEntity<ChatProvider.SettlementRoom> responseEntity = ResponseEntity.ok(room);
            return new BaseResponse<>(responseEntity);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 정산하기 - 정산 요청
    @ResponseBody
    @GetMapping("/grouppurchase/settlement/require")
    public BaseResponse<Object> setAmountRequire(@RequestParam(value = "chatRoomIdx")Long chatRoomIdx) {
        return new BaseResponse<>("정산 요청 버튼 눌렀을 때");
    }


    // 거래 완료
    @ResponseBody
    @PostMapping("/grouppurchase/{chatRoomIdx}/complete-deal")
    public BaseResponse<Object> requestSettlement(@PathVariable("chatRoomIdx")Long chatRoomIdx, @RequestBody GetChatUser getChatUser) {
        try {
            Object x = chatService.requestSettlement(chatRoomIdx, getChatUser);
            return new BaseResponse<>(x);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    // 정산 완료
//    @ResponseBody
//    @PostMapping("/grouppurchase/{chatRoomIdx}/complete-settlement")
//    public BaseResponse<Object> completeSettlement(@PathVariable("chatRoomIdx")Long chatRoomIdx, @RequestBody GetChatUser getChatUser) {
//        try {
//            Object x = chatService.completeSettlement();
//            return new BaseResponse<>(x);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }


    // 모든거래완료
    @ResponseBody
    @DeleteMapping("/grouppurchase/{chatRoomIdx}/finalize-settlement")
    public BaseResponse<Object> finalizeSettlement(@PathVariable("chatRoomIdx") Long chatRoomIdx) {
        try {
            Object x = chatService.deleteChatRoom(chatRoomIdx);
            return new BaseResponse<>(x);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
