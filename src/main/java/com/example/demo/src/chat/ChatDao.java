package com.example.demo.src.chat;

import com.example.demo.src.chat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<Object> getChatRooms(Long userIdx, String category) {

        String getUserQuery = "SELECT chatRoomIdx FROM ChatUser WHERE userIdx = ?";
        List<Long> chatRoomIdxList = this.jdbcTemplate.query(getUserQuery, new Object[]{userIdx},
                (rs, rowNum) -> rs.getLong("chatRoomIdx")
        );

        String getChatRoomsQuery = null;
        if (category.equals("community")) {
            getChatRoomsQuery = "SELECT cr.chatRoomIdx, cu.userIdx, p.title, cr.unreads, cc.createTime, cc.contents " +
                    "FROM Post p " +
                    "JOIN ChatRoom cr On p.postIdx = cr.postIdx " +
                    "JOIN ChatUser cu on cr.chatRoomIdx = cu.chatRoomIdx " +
                    "JOIN ChatComment cc on cu.chatUserIdx = cc.chatUserIdx " +
                    "WHERE p.categoryIdx BETWEEN 10 AND 19 AND cu.chatRoomIdx IN (?)" +
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

    public Object getChatRoom(Long chatRoomIdx) {
        String getChatRoomQuery = "SELECT category, title " +
                "FROM Post p " +
                "JOIN ChatRoom c On p.postIdx = c.postIdx " +
                "JOIN PostCategory pc On pc.categoryIdx = p.categoryIdx " +
                "WHERE chatRoomIdx = " + chatRoomIdx;
        return this.jdbcTemplate.query(getChatRoomQuery,
                (rs, rowNum) -> new GetInChatRoom(
                        rs.getString("category"),
                        rs.getString("title"))
        );
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
                        "VALUES(?, ?, ?, 0, 0, now(), now(), 0)";

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
                        rs.getString("contentType"))
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
        return getChatRoomUsers;
    }



    public int getChatRoomMembers(Long chatRoomIdx) {
        String getChatRoomMembersQuery = "SELECT members FROM ChatRoom WHERE chatRoomIdx = ?";
        int members = this.jdbcTemplate.queryForObject(getChatRoomMembersQuery, new Object[]{chatRoomIdx}, Integer.class);
        return members;
    }


    public void saveChatComment(PostChatComment postChatComment) {
        String saveChatCommentQuery = "INSERT INTO ChatComment" +
                "(chatCommentIdx, chatUserIdx, chatRoomIdx, contents, kind, unread, createTime, contentType)" +
                "VALUES (?, ?, ?, ?, ?, 0, now(), ?)";
        this.jdbcTemplate.update(saveChatCommentQuery,
                postChatComment.getChatCommentIdx(),
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