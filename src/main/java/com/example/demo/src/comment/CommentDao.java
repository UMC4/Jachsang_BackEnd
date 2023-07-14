package com.example.demo.src.comment;

import com.example.demo.src.comment.model.Comment;
import com.example.demo.src.comment.model.CommentingReq;
import com.example.demo.src.comment.model.EditCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CommentDao {
    // 댓글 달기
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public CommentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int commenting(CommentingReq commeningReq) {
        String sql = "INSERT INTO Comment(postIdx,parentCommentIdx,userIdx,contents,likeCount,createAt,updateAt)"
                +"VALUES (?,?,?,?,0,now(),now())";
        Object[] param = { commeningReq.getPostIdx(), commeningReq.getParentCommentIdx(),
                commeningReq.getUserIdx(),commeningReq.getContents()};
        return this.jdbcTemplate.queryForObject(sql,param,int.class);
    }

    // 댓글 수정하기
    public int editComment(EditCommentReq editCommentReq){
        if(editCommentReq.getContents() == null || editCommentReq.getContents().replace(" ","").equals("")) return 0;
        String sql = "UPDATE Comment SET comment = "+editCommentReq.getContents()+" WHERE commentIdx = " +editCommentReq.getCommentIdx();
        return this.jdbcTemplate.update(sql);
    }

    // 댓글에 좋아요 남기기
    public int likeComment(int commentIdx){
        String sql = "UPDATE Comment SET likeCount = likeCount + 1 WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.update(sql);
    }
    public int cancelLikeComment(int commentIdx){
        String sql = "UPDATE Comment SET likeCount = likeCount - 1 WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.update(sql);
    }
    public Comment getComment(int commentIdx) {
        String sql = "SELECT * FROM Comment(postIdx,userIdx,contents,parentCommentIdx,createAt,updateAt,likeCount) WHERE commentIdx = " + commentIdx;
        return this.jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Comment(
                commentIdx,
                rs.getInt("postIdx"),
                rs.getInt("parentCommentIdx"),
                rs.getInt("userIdx"),
                rs.getInt("likeCount"),
                rs.getString("contents"),
                rs.getTimestamp("createAt"),
                rs.getTimestamp("updateAt")
        ));
    }
}
