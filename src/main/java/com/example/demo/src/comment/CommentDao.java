package com.example.demo.src.comment;

import com.example.demo.src.comment.model.*;
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

    // 댓글 작성하기 - parent = 0, originIdx = commentIdx;
    // parentIdx 자동으로 들어가게 수정하셈
    public int commenting(CommentingReq commentingReq) {
        // 댓글을 생성하는 sql과 변수
        String createCommentSql = "INSERT INTO Comment(postIdx,userIdx,parentCommentIdx,originIdx,contents)" +
                "VALUES (?,?,0,0,?)";
        Object[] param = {
                commentingReq.getPostIdx(), commentingReq.getUserIdx(), commentingReq.getContents()
        };
        // 댓글 생성 sql 실행
        this.jdbcTemplate.update(createCommentSql,param);
        // originIdx 설정을 위해 Idx 뽑아내는 sql
        String lastInsertIdQuery = "select last_insert_id()";
        // commentIdx 추출
        int commentIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
        // originIdx = commentIdx을 실현하는 sql
        String setOriginIdxSql = "UPDATE Comment SET originIdx = "+commentIdx+" WHERE commentIdx = "+commentIdx;
        // 이것을 실행한 결과 리턴
        return this.jdbcTemplate.update(setOriginIdxSql);
    }
    // 댓글 수정하기
    public int editComment(EditCommentReq editCommentReq){
        if(editCommentReq.getContents() == null || editCommentReq.getContents().replace(" ","").equals("")) return 0;
        String sql = "UPDATE Comment SET contents = \""+editCommentReq.getContents()+"\", updateAt = CURRENT_TIMESTAMP WHERE commentIdx = " +editCommentReq.getCommentIdx();
        return this.jdbcTemplate.update(sql);
    }
    // 댓글에 좋아요 남기기 -> 예외처리 해야됨
    public int likeComment(LikeReq likeReq){
        String sql = "UPDATE Comment SET likeCount = likeCount + 1 WHERE commentIdx = "+likeReq.getCommentIdx();
        String addLikeHistorySql = "INSERT INTO HeartComment(userIdx, commentIdx) VALUES ("+likeReq.getUserIdx()+", "+likeReq.getCommentIdx()+")";
        this.jdbcTemplate.update(addLikeHistorySql);
        return this.jdbcTemplate.update(sql);
    }
    // 댓글 좋아요 취소 -> 예외처리 해야됨
    public int cancelLikeComment(LikeReq likeReq){
        String sql = "UPDATE Comment SET likeCount = likeCount - 1 WHERE commentIdx = "+likeReq.getCommentIdx();
        String subLikeHistorySql = "DELETE FROM HeartComment WHERE userIdx = "+likeReq.getUserIdx()+" AND commentIdx = "+likeReq.getCommentIdx();
        this.jdbcTemplate.update(subLikeHistorySql);
        return this.jdbcTemplate.update(sql);
    }
    // 댓글 조회하기 -> 삭제된 댓글은 isDeleted가 true이므로 프론트에서 예외처리 바람.
    public Comment getComment(int commentIdx) {
        String sql = "SELECT * FROM Comment " +
                "WHERE commentIdx = " + commentIdx;
        //조회하려는 댓글이 삭제된 경우
        return this.jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Comment(
                commentIdx,
                rs.getInt("postIdx"),
                rs.getInt("userIdx"),
                rs.getInt("parentCommentIdx"),
                rs.getInt("originIdx"),
                rs.getInt("likeCount"),
                rs.getBoolean("isDeleted"),
                rs.getString("contents"),
                rs.getTimestamp("createAt"),
                rs.getTimestamp("updateAt")
        ));

    }
    // 답글 작성하기
    public int replying(ReplyReq replyReq){
        String getLastReplyIdx = "SELECT commentIdx FROM Comment WHERE originIdx = "+replyReq.getOriginIdx()+" ORDER BY createAt DESC LIMIT 1";
        int parentCommentIdx = this.jdbcTemplate.queryForObject(getLastReplyIdx,int.class);

        String replySql = "INSERT INTO Comment(postIdx,userIdx,parentCommentIdx,originIdx,contents)"+
                " VALUES (?,?,?,?,?)";
        Object[] param = {
                replyReq.getPostIdx(), replyReq.getUserIdx(), parentCommentIdx,
                replyReq.getOriginIdx(), replyReq.getContents()};

        return this.jdbcTemplate.update(replySql,param);
    }
    //댓글 삭제하기
    public int deleteComment(int commentIdx){
        // 공감 댓글 목록에서 해당 댓글 삭제 (답글도 마찬가지)
        String deleteLikeHistorySql = "DELETE FROM HeartComment WHERE commentIdx = "+commentIdx;
        this.jdbcTemplate.update(deleteLikeHistorySql);
        // 부모인덱스 추출, 원 댓글 : 0, 답글 : 원 댓글의 commentIdx
        int originIdx = _getOriginIdxOf(commentIdx);
        // 자신을 제외한 모든 댓글이 지워졌는지 확인
        String checkAllDeletionSql = "SELECT NOT EXISTS(SELECT 1 FROM Comment WHERE originIdx = "+originIdx+"" +
                " AND isDeleted = false AND commentIdx != "+commentIdx+")";
        int allDeletionResult = this.jdbcTemplate.queryForObject(checkAllDeletionSql,int.class);
        // 자신 제외하고 다 삭제된 상황
        if(allDeletionResult == 1) {
            //뿌리부터 꼬리까지 전부 DB에서 삭제
            String deletionSql = "DELETE FROM Comment WHERE originIdx = "+originIdx;
            return this.jdbcTemplate.update(deletionSql);
        }
        // 남은 댓글/답글이 존재하는 상황
        else {
            // 삭제하려는 댓글의 isDeleted를 true로 변경함.
            String deletionSql = "UPDATE Comment SET isDeleted = true, updateAt = now() WHERE commentIdx = "+commentIdx;
            return this.jdbcTemplate.update(deletionSql);
        }
    }

    ////////////////////////////// 내부 메서드 //////////////////////////////
    public int _getOriginIdxOf(int commentIdx){
        String getOriginIdxSql = "SELECT originIdx From Comment WHERE commentIdx = "+commentIdx;
        return this.jdbcTemplate.queryForObject(getOriginIdxSql,int.class);
    }
    public int _getParentIdxOf(int commentIdx){
        int originIdx = _getOriginIdxOf(commentIdx);
        String getParentIdxSql = "SELECT commentIdx From Comment WHERE originIdx = "+originIdx +
                "ORDER BY createAt DESC";
        return this.jdbcTemplate.queryForObject(getParentIdxSql,int.class);
    }

    public boolean _isAlreadyLikeComment(int userIdx, int commentIdx){
        String checkSql = "SELECT EXISTS (SELECT 1 FROM HeartComment WHERE commentIdx = "+commentIdx+" AND userIdx = "+userIdx+")";
        return this.jdbcTemplate.queryForObject(checkSql,int.class) == 1 ? true : false;
    }
}
