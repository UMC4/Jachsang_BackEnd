package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@RequiredArgsConstructor
@Service
public class ChatProvider {

    private final ChatDao chatDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    public List<Object> getChatRooms(GetUser getUser, String category) throws BaseException {
        try {
            Long userIdx = getUser.getUserIdx();
            List<Object> getChatRoom = chatDao.getChatRooms(userIdx, category);
            getChatRoom.removeIf(chatRoom -> Objects.equals(((GetChatRooms) chatRoom).getUserIdx(), userIdx));

            Set<Long> uniqueChatRoomIdx = new HashSet<>();
            List<Object> filteredChatRooms = new ArrayList<>();

            for (Object chatRoom : getChatRoom) {
                GetChatRooms chatRoomObj = (GetChatRooms) chatRoom;
                Long chatRoomIdx = chatRoomObj.getChatRoomIdx();

                if (!uniqueChatRoomIdx.contains(chatRoomIdx)) {
                    uniqueChatRoomIdx.add(chatRoomIdx);
                    filteredChatRooms.add(chatRoom);
                }
            }
            return filteredChatRooms;
        }
        catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Object getChatRoom(Long chatRoomIdx) throws BaseException {
        try {
            Object getChatRoom = chatDao.getChatRoom(chatRoomIdx);
            return getChatRoom;
        } catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public List<GetChatComment> getChatComment(Long chatRoomIdx) throws BaseException {
        try {
            List<GetChatComment> getChatComment = chatDao.getChatComment(chatRoomIdx);

            getChatComment.removeIf(comment -> comment.getReported() > 0);

            return getChatComment;
        } catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<String> getChatRoomUsers(Long chatRoomIdx) throws BaseException {
        try {
            List<String> getChatRoomUsers = chatDao.getChatRoomUsers(chatRoomIdx);
            return getChatRoomUsers;
        } catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }



    public int getChatRoomMembers(Long chatRoomIdx) throws BaseException {
        try {
            int members = chatDao.getChatRoomMembers(chatRoomIdx);
            return members;
        } catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public static class SettlementRoom {
        private int totalMembers;
        private int totalAmount;
        private int[] individualAmounts;

        public SettlementRoom(int totalMembers) {
            this.totalMembers = totalMembers;
            this.individualAmounts = new int[totalMembers];
        }

        public void setTopAmount(int amount) {
            this.totalAmount = amount;
            int individualShare = amount / totalMembers;
            int remainingAmount = amount % totalMembers;
            for (int i = 0; i < totalMembers; i++) {
                individualAmounts[i] = individualShare;
                if (remainingAmount > 0) {
                    individualAmounts[i]++;
                    remainingAmount--;
                }
            }
        }

        public void setIndividualAmounts(int[] amounts) {
            int sum = 0;
            for (int amount : amounts) {
                sum += amount;
            }
            this.totalAmount = sum;
            int remainingAmount = sum % totalMembers;
            for (int i = 0; i < totalMembers; i++) {
                individualAmounts[i] = amounts[i];
                if (remainingAmount > 0) {
                    individualAmounts[i]++;
                    remainingAmount--;
                }
            }
        }

        public int getTotalMembers() {
            return totalMembers;
        }
        public int getTotalAmount() {
            return totalAmount;
        }
        public int[] getIndividualAmounts() {
            return individualAmounts;
        }
    }


}
