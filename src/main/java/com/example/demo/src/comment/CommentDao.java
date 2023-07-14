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
        String sql = "INSERT INTO (postIdx,userIdx," +
                "parentCommentIdx,childCommentIdx,commentFamily," +
                "likeCount,contents,createAt,updateAt)"
                +"VALUES (?,?,?,0,?,?,0,now(),now())";
        Object[] param = { commeningReq.getPostIdx(), commeningReq.getUserIdx(),
                commeningReq.getParentCommentIdx(), commeningReq.getContents()};
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
        String sql = "SELECT * FROM Comment(postIdx,userIdx,parentCommentIdx,childCommentIdx," +
                "commentFamily,likeCount,contents,createAt,updateAt) " +
                "WHERE commentIdx = " + commentIdx;
        return this.jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Comment(
                commentIdx,
                rs.getInt("postIdx"),
                rs.getInt("userIdx"),
                rs.getInt("parentCommentIdx"),
                rs.getInt("childCommentIdx"),
                rs.getInt("commentFamily"),
                rs.getInt("likeCount"),
                rs.getString("contents"),
                rs.getTimestamp("createAt"),
                rs.getTimestamp("updateAt")
        ));
    }

    public int deleteComment(int commentIdx) {
        int parentIdx = _getParentCommentIdx(commentIdx);
        int childIdx = _getChildCommentIdx(commentIdx);
        String deleteSql;
        // 최상위 부모 댓글인 경우
        if(parentIdx == 0) {
            // 자식 댓글이 없는 경우 -> 완전 삭제
            if(childIdx == 0)
                deleteSql = "DELETE FROM Comment WHERE commentIdx = "+
        }

        return 0;
    }

    public int _getParentCommentIdx(int commentIdx){
        String getParentIdxSql = "SELECT parentCommentIdx FROM Comment WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getParentIdxSql,int.class);
    }
    public int _getChildCommentIdx(int commentIdx){
        String getChildIdxSql = "SELECT childCommentIdx FROM Comment WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getChildIdxSql,int.class);
    }
}
