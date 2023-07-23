package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
            return getChatRoom;
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
            return getChatComment;
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
        private int topAmount;

        public SettlementRoom(int totalMembers) {
            this.totalMembers = totalMembers;
            this.individualAmounts = new int[totalMembers];
            this.topAmount = 0;
        }

        public void setTopAmount(int amount) {
            this.topAmount = amount;
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

        public int getTopAmount() {
            return topAmount;
        }
    }


}
