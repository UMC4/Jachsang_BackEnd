package com.example.demo.src.chat;

import com.example.demo.src.chat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<PostChatRoom> findAllChatRooms() {
        String getChatRoomsQuery = "SELECT cr.chatRoomIdx, p.userIdx, p.title, cr.unreads, cr.updateTime " +
                "FROM ChatRoom cr " +
                "Join Post p On p.postIdx = cr.postIdx";
        List<PostChatRoom> postChatRooms = jdbcTemplate.query(getChatRoomsQuery,
                (rs, rowNum) -> PostChatRoom.builder()
                        .chatRoomIdx(rs.getLong("chatRoomIdx"))
                        .userIdx(rs.getLong("userIdx"))
                        .title(rs.getString("title"))
                        .unreads(rs.getInt("unreads"))
                        .updateTime(rs.getTimestamp("updateTime"))
                        .build());
        return postChatRooms;
    }



    public Object getUser(GetUser getUser) {
        String getUserQuery = "SELECT userIdx, chatRestrictTime " +
                "FROM User " +
                "WHERE userIdx = " + getUser.getUserIdx();
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetUser(
                        rs.getLong("userIdx"),
                        rs.getTimestamp("chatRestrictTime"))
                );
    }


    public List<Object> getChatRooms(Long userIdx, String category) {

        String getUserQuery = "SELECT chatRoomIdx FROM ChatUser WHERE userIdx = ?";
        List<Long> chatRoomIdxList = this.jdbcTemplate.query(getUserQuery, new Object[]{userIdx},
                (rs, rowNum) -> rs.getLong("chatRoomIdx")
        );

        if (chatRoomIdxList.isEmpty()) {
            return null;
        }

        String getChatRoomsQuery = null;
        if (category.equals("community")) {
            getChatRoomsQuery = "SELECT cr.chatRoomIdx, cu.userIdx, p.title, cr.unreads, cc.createTime, cc.contents " +
                    "FROM Post p " +
                    "JOIN ChatRoom cr On p.postIdx = cr.postIdx " +
                    "JOIN ChatUser cu on cr.chatRoomIdx = cu.chatRoomIdx " +
                    "JOIN ChatComment cc on cu.chatUserIdx = cc.chatUserIdx " +
                    "WHERE p.categoryIdx BETWEEN 10 AND 19 AND cu.chatRoomIdx In (?) " +
                    "ORDER BY cc.createTime DESC ";
        } else if (category.equals("grouppurchase")) {
            getChatRoomsQuery = "SELECT cr.chatRoomIdx, cu.userIdx, p.title, cr.unreads, cc.createTime, cc.contents " +
                    "FROM Post p " +
                    "JOIN ChatRoom cr On p.postIdx = cr.postIdx " +
                    "JOIN ChatUser cu on cr.chatRoomIdx = cu.chatRoomIdx " +
                    "JOIN ChatComment cc on cu.chatUserIdx = cc.chatUserIdx " +
                    "WHERE p.categoryIdx BETWEEN 20 AND 29 AND cu.chatRoomIdx In (?) " +
                    "ORDER BY cc.createTime DESC";
        }
        if (getChatRoomsQuery == null)
            return null;

        String chatRoomIdxStr = String.join(",", Collections.nCopies(chatRoomIdxList.size(), "?"));
        getChatRoomsQuery = getChatRoomsQuery.replace("?", chatRoomIdxStr);

        List<Object> chatRoomList = this.jdbcTemplate.query(getChatRoomsQuery, chatRoomIdxList.toArray(),
                (rs, rowNum) -> new GetChatRooms(
                        rs.getLong("chatRoomIdx"),
                        rs.getLong("userIdx"),
                        rs.getString("title"),
                        rs.getInt("unreads"),
                        rs.getTimestamp("createTime"),
                        rs.getString("contents")
                )
        );

        return chatRoomList;
    }


    public List<Long> existInChatRoom(Long chatRoomIdx) {
        String existInChatRoomQuery = "SELECT userIdx FROM ChatUser WHERE chatRoomIdx = ?";
        List<Long> existInChatRoom = this.jdbcTemplate.query(existInChatRoomQuery, new Object[]{chatRoomIdx},
                (rs, rowNum) -> rs.getLong("userIdx"));

        return existInChatRoom;
    }


    public Object getChatRoom(Long chatRoomIdx) {
        String getChatRoomQuery = "SELECT category, title " +
                "FROM Post p " +
                "JOIN ChatRoom c On p.postIdx = c.postIdx " +
                "JOIN PostCategory pc On pc.categoryIdx = p.categoryIdx " +
                "WHERE chatRoomIdx = " + chatRoomIdx;
        Object getChatRoom = this.jdbcTemplate.query(getChatRoomQuery,
                (rs, rowNum) -> new GetInChatRoom(
                        rs.getString("category"),
                        rs.getString("title"))
                );

        if (ObjectUtils.isEmpty(getChatRoom)) {
            return null;
        }

        return getChatRoom;
    }


    public Long getChatRoomByPostIdx(Long postIdx) {
        String getChatRoomQuery = "SELECT chatRoomIdx FROM ChatRoom WHERE postIdx = ?";
        try {
            Long existingChatRoomIdx = this.jdbcTemplate.queryForObject(getChatRoomQuery, new Object[]{postIdx}, Long.class);
            return existingChatRoomIdx;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addUserToChatRoom(Long chatUserIdx, Long chatRoomIdx, Long userIdx) {
        String addUserToChatRoomQuery = "INSERT INTO ChatUser(chatUserIdx, userIdx, createTime, chatRoomIdx) " +
                "VALUES(?, ?, now(), ?)";
        Object[] params = {chatUserIdx, userIdx, chatRoomIdx};

        String updateChatRoomMembersQuery = "UPDATE ChatRoom SET members = members + 1, " +
                "groupPurchaseMembers = groupPurchaseMembers + 1 WHERE chatRoomIdx = ?";
        this.jdbcTemplate.update(updateChatRoomMembersQuery, chatRoomIdx);

        this.jdbcTemplate.update(addUserToChatRoomQuery, params);
    }


    public PostChatUser postChatUser(Long chatUserIdx, Long userIdx, Long chatRoomIdx) {

        String postChatUserQuery = "INSERT INTO ChatUser(chatUserIdx, userIdx, createTime, chatRoomIdx) " +
                "VALUES(?, ?, now(), ?)";

        Object[] params = {chatUserIdx, userIdx, chatRoomIdx};

        this.jdbcTemplate.update(postChatUserQuery, params);

        return new PostChatUser(chatUserIdx, userIdx, new Timestamp(System.currentTimeMillis()), chatRoomIdx);
    }

    public PostChatRoom postChatRoom(PostChatRoom postChatRoom, GetPost getPost) {
        String postChatRoomQuery =
                "INSERT INTO ChatRoom(chatRoomIdx, postIdx, host, unreads, members, createTime, updateTime, groupPurchaseMembers) " +
                        "VALUES(?, ?, ?, 0, 2, now(), now(), 2)";

        Object[] params = {postChatRoom.getChatRoomIdx(), getPost.getPostIdx(), getPost.getUserIdx()};

        this.jdbcTemplate.update(postChatRoomQuery, params);

        return postChatRoom;
    }


    public List<GetChatComment> getChatComment(Long chatRoomIdx) {
        String getChatCommentQuery = "SELECT * FROM ChatComment WHERE chatRoomIdx = " + chatRoomIdx;
        return this.jdbcTemplate.query(getChatCommentQuery,
                (rs, rowNum) -> new GetChatComment(
                        rs.getLong("chatCommentIdx"),
                        rs.getLong("chatRoomIdx"),
                        rs.getLong("chatUserIdx"),
                        rs.getString("contents"),
                        rs.getString("kind"),
                        rs.getInt("unread"),
                        rs.getTimestamp("createTime"),
                        rs.getString("contentType"),
                        rs.getInt("reported"))
        );
    }

    public List<String> getChatRoomUsers(Long chatRoomIdx) {
        String getChatRoomUsersQuery = "SELECT u.nickname " +
                "FROM ChatRoom cr " +
                "JOIN ChatUser cu ON cr.chatRoomIdx = cu.chatRoomIdx " +
                "JOIN User u ON u.userIdx = cu.userIdx " +
                "WHERE cr.chatRoomIdx = ?";

        List<String> getChatRoomUsers = this.jdbcTemplate.queryForList(
                getChatRoomUsersQuery,
                new Object[]{chatRoomIdx},
                String.class
        );

        if (getChatRoomUsers.isEmpty()) {
            return null;
        }

        return getChatRoomUsers;
    }



    public int getChatRoomMembers(Long chatRoomIdx) {
        String getChatRoomMembersQuery = "SELECT members FROM ChatRoom WHERE chatRoomIdx = ?";
        List<Integer> result = this.jdbcTemplate.query(getChatRoomMembersQuery, new Object[]{chatRoomIdx},
                (rs, rowNum) -> rs.getInt("members"));

        if (result.isEmpty()) {
            return 0;
        }
        int members = result.get(0);
        return members;
    }


    public List<Long> existChatRoom() {
        String existChatRoomQuery = "SELECT chatRoomIdx FROM ChatRoom";
        return this.jdbcTemplate.queryForList(existChatRoomQuery, Long.class);
    }


    public void saveChatComment(PostChatComment postChatComment, Long chatCommentIdx) {
        String saveChatCommentQuery = "INSERT INTO ChatComment" +
                "(chatCommentIdx, chatUserIdx, chatRoomIdx, contents, kind, unread, createTime, contentType)" +
                "VALUES (?, ?, ?, ?, ?, 0, now(), ?)";
        this.jdbcTemplate.update(saveChatCommentQuery,
                chatCommentIdx,
                postChatComment.getChatUserIdx(),
                postChatComment.getChatRoomIdx(),
                postChatComment.getContents(),
                postChatComment.getKind(),
                postChatComment.getContentType());
    }



    public boolean getGroupPurchaseCheck(Long chatRoomIdx, GetChatUser getChatUser) {
        String getGroupPurchaseCheckQuery = "SELECT groupPurchaseCheck " +
                "FROM ChatUser cu " +
                "JOIN ChatRoom cr ON cu.chatRoomIdx = cr.chatRoomIdx " +
                "WHERE cr.chatRoomIdx = ? AND cu.chatUserIdx = ?";

        Boolean groupPurchaseCheck = this.jdbcTemplate.queryForObject(
                getGroupPurchaseCheckQuery,
                new Object[]{chatRoomIdx, getChatUser.getChatUserIdx()},
                Boolean.class);
        return groupPurchaseCheck;
    }


    public int updateGroupPurchaseCheck(Long chatRoomIdx, GetChatUser getChatUser) {
        String updateGroupPurchaseCheckQuery = "UPDATE ChatUser SET groupPurchaseCheck = " + true +
                " WHERE chatRoomIdx = ? AND chatUserIdx = ?";

        this.jdbcTemplate.update(updateGroupPurchaseCheckQuery, chatRoomIdx, getChatUser.getChatUserIdx());

        String selectGroupPurchaseMembersQuery = "SELECT groupPurchaseMembers FROM ChatRoom WHERE chatRoomIdx = ?";

        int groupPurchaseMembers = this.jdbcTemplate.queryForObject(selectGroupPurchaseMembersQuery,
                new Object[]{chatRoomIdx}, Integer.class);
        groupPurchaseMembers = groupPurchaseMembers - 1;

        String updateGroupPurchaseMembersQuery = "UPDATE ChatRoom SET groupPurchaseMembers = ? " +
                "WHERE chatRoomIdx = ?";

        this.jdbcTemplate.update(updateGroupPurchaseMembersQuery, groupPurchaseMembers, chatRoomIdx);

        return groupPurchaseMembers;
    }

    public void deleteChatUser(GetChatUser getChatUser) {
        String deleteChatCommentQuery = "DELETE FROM ChatComment WHERE chatUserIdx = " + getChatUser.getChatUserIdx();
        this.jdbcTemplate.update(deleteChatCommentQuery);

        String deleteChatUserQuery = "DELETE FROM ChatUser WHERE chatUserIdx = " + getChatUser.getChatUserIdx();
        this.jdbcTemplate.update(deleteChatUserQuery);
    }



    public int getGroupPurchaseMembers(Long chatRoomIdx) {
        String getGroupPurchaseMembersQuery = "SELECT groupPurchaseMembers FROM ChatRoom WHERE chatRoomIdx = ?";

        int groupPurchaseMembers = this.jdbcTemplate.queryForObject(getGroupPurchaseMembersQuery,
                new Object[]{chatRoomIdx}, Integer.class);
        return groupPurchaseMembers;
    }


    public boolean hostCheck(Long chatRoomIdx, GetChatUser getChatUser) {
        String hostCheckQuery = "SELECT host FROM ChatRoom WHERE chatRoomIdx = ?";
        int hostCheck = this.jdbcTemplate.queryForObject(hostCheckQuery,
                new Object[]{chatRoomIdx}, Integer.class);

        return hostCheck == getChatUser.getUserIdx();
    }


    public Object deleteChatRoom(Long chatRoomIdx) {
        String deleteChatCommentQuery = "DELETE FROM ChatComment WHERE chatRoomIdx = " + chatRoomIdx;
        this.jdbcTemplate.update(deleteChatCommentQuery);

        String deleteChatUserQuery = "DELETE FROM ChatUser WHERE chatRoomIdx = " + chatRoomIdx;
        this.jdbcTemplate.update(deleteChatUserQuery);

        String deleteChatRoomQuery = "DELETE FROM ChatRoom WHERE chatRoomIdx = " + chatRoomIdx;
        this.jdbcTemplate.update(deleteChatRoomQuery);
        return 0;
    }


}