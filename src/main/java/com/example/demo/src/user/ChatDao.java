package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
            getChatRoomsQuery = "SELECT cr.chatRoomIdx, cu.userIdx, p.title, cr.unreads, cr.updateTime " +
                    "FROM Post p " +
                    "JOIN ChatRoom cr On p.postIdx = cr.postIdx " +
                    "JOIN ChatUser cu on cr.chatRoomIdx = cu.chatRoomIdx " +
                    "WHERE p.categoryIdx BETWEEN 10 AND 19 AND cu.chatRoomIdx In (?) " +
                    "ORDER BY cr.updateTime DESC";
        } else if (category.equals("grouppurchase")) {
            getChatRoomsQuery = "SELECT cr.chatRoomIdx, cu.userIdx, p.title, cr.unreads, cr.updateTime " +
                    "FROM Post p " +
                    "JOIN ChatRoom cr On p.postIdx = cr.postIdx " +
                    "JOIN ChatUser cu on cr.chatRoomIdx = cu.chatRoomIdx " +
                    "WHERE p.categoryIdx BETWEEN 20 AND 29 AND cu.chatRoomIdx In (?) " +
                    "ORDER BY cr.updateTime DESC";
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
                        rs.getTimestamp("updateTime")
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


    public PostChatUser postChatUser(Long chatUserIdx, GetUser getUser, Long chatRoomIdx) {

        String postChatUserQuery = "INSERT INTO ChatUser(chatUserIdx, userIdx, createTime, chatRoomIdx) " +
                "VALUES(?, ?, now(), ?)";

        Object[] params = {chatUserIdx, getUser.getUserIdx(), chatRoomIdx};

        this.jdbcTemplate.update(postChatUserQuery, params);

        return new PostChatUser(chatUserIdx, getUser.getUserIdx(), new Timestamp(System.currentTimeMillis()), chatRoomIdx);
    }

    public PostChatRoom postChatRoom(PostChatRoom postChatRoom, GetPost getPost) {
        String postChatRoomQuery =
                "INSERT INTO ChatRoom(chatRoomIdx, postIdx, unreads, members, createTime, updateTime, groupPurchaseMembers) " +
                        "VALUES(?, ?, 0, 0, now(), now(), 0)";

        Object[] params = {postChatRoom.getChatRoomIdx(), getPost.getPostIdx()};

        this.jdbcTemplate.update(postChatRoomQuery, params);

        return postChatRoom;
    }


    public List<GetChatComment> getChatComment(Long chatRoomIdx) {
        String getChatCommentQuery = "SELECT * FROM ChatComment WHERE chatRoomIdx = " + chatRoomIdx;
        return this.jdbcTemplate.query(getChatCommentQuery,
                (rs, rowNum) -> new GetChatComment(
                        rs.getLong("chatCommentIdx"),
                        rs.getLong("chatRoomIdx"),
                        rs.getLong("userIdx"),
                        rs.getString("contents"),
                        rs.getString("kind"),
                        rs.getInt("unread"),
                        rs.getTimestamp("createTime"),
                        rs.getString("contentType"))
        );
    }

    public int getChatRoomMembers(Long chatRoomIdx) {
        String getChatRoomMembersQuery = "SELECT members FROM ChatRoom WHERE chatRoomIdx = ?";
        int members = this.jdbcTemplate.queryForObject(getChatRoomMembersQuery, new Object[]{chatRoomIdx}, Integer.class);
        return members;
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

        int groupPurchaseMembers = this.jdbcTemplate.queryForObject(selectGroupPurchaseMembersQuery, new Object[]{chatRoomIdx}, Integer.class);
        groupPurchaseMembers = groupPurchaseMembers - 1;

        String updateGroupPurchaseMembersQuery = "UPDATE ChatRoom SET groupPurchaseMembers = ? " +
                "WHERE chatRoomIdx = ?";

        this.jdbcTemplate.update(updateGroupPurchaseMembersQuery, groupPurchaseMembers, chatRoomIdx);

        return groupPurchaseMembers;
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