package com.example.demo.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),


    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, HttpStatus.BAD_REQUEST.value(), "입력값을 확인해주세요."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),

    // users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),

    /**
     * 50 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),

//<<<<<<< HEAD

    /**
     * 1000: 유저 관련 오류
     */


    //유저
    NEEDED_EMAIL_INPUT(false,1000, "이메일을 입력해주세요."),
    PERMANENT_BANNED_USER(false, 1001, "영구정지된 회원입니다."),
    NOT_INPUT_PWD(false,1003,"비밀번호를 입력하지 않았습니다."),
    FAILED_GET_USERS(false,1004,"유저들을 가져오기에 실패하였습니다."),
    FAILED_GET_USERIDX(false,1005,"유저 인덱스 가져오기에 실패하였습니다."),
    FAILED_GET_ID(false,1006,"유저아이디 찾기에 실패하였습니다."),
    FAILED_GET_CHATROOM(false,1007,"유저 채팅방을 불러오는데 실패하였습니다."),
    FAILED_TO_FOLLOW(false,1008,"유저 팔로우하는데 실패하였습니다."),
    FAILED_TO_GETUSER(false,1008,"유저를 불러오는데 실패하였습니다."),
    ID_ALREATY_EXISTS(false,1009,"아이디가 이미 존재합니다."), //Email Controller
    FAILED_TO_GETMAIL(false,1010,"유저 이메일을 체크 실패하였습니다."),
    FAILED_CHECK_FOLLOWED(false,1011,"팔로우 여부체크에 실패하였습니다."),
    FAILED_CHECK_EXISTS_ID(false,1012,"아이디 존재 여부체크에 실패하였습니다."),
    FOLLOWED_USER_ALREADY(false,1013,"이미 팔로우 하였습니다."),
    FAILED_TO_UNFOLLOW(false,1014,"팔로우 취소에 실패하였습니다."),
    MODIFY_FAIL_USERPWD(false,1015,"비밀번호 재설정에 실패하였습니다."),
    MODIFY_FAIL_USERINFO(false,1016,"유저 정보변경에 실패하였습니다."),
    FAILED_SEND_EMAIL(false,1017,"인증코드 전송에 실패하였습니다."),
    JWT_USER_MISSMATCH(false,1018,"jwt값과 userIdx값이 가리키는 유저가 다릅니다."),

    //게시글
    NOT_EXIST_POST_IDX(false,3000,"존재하는 게시글idx가 아닙니다."),
    OVER_LENGTH(false, 3001, "내용이 정해진 길이를 초과했습니다."),
    WRONG_CATEGORY(false, 3002, "존재하지 않는 카테고리입니다."),
    PERMISSION_DENIED(false, 3003, "작업 권한이 없는 유저입니다."),
    OMITTED_PARAMETER(false,3004,"파라미터가 누락되었습니다."),
    ALREADY_CALCULATED(false,3005,"이미 정산 완료된 공동구매입니다."),
    NOT_EXIST_COMMENT_IDX(false,3006,"존재하지 않는 댓글 idx입니다."),
    SELF_REPORT(false,3007,"자기 자신을 신고할 수 없습니다."),
    REPORT_COUNT_OVER(false,3009,"이미 신고한 대상입니다."),
    SELF_ADDITION(false,3010,"자신의 글에 공감/좋아요를 남길 수 없습니다."),
    INCOMPLETE_POST(false,3011,"불완전한 게시글입니다. 게시글을 삭제합니다."),

    //게시판
    BOTH_CATEGORY_SORT_INPUT(false, 2000, "카테고리와 정렬 조건 중 하나만 입력되어야 합니다."),
    NO_CATEGORY_SORT_INPUT(false, 2001, "카테고리나 정렬 조건이 입력되어야 합니다."),
    NOT_EXIST_CATEGORY(false, 2002, "입력된 카테고리 값이 카테고리 목록에 존재하지 않습니다."),
    NOT_EXIST_SORT(false, 2003, "입력된 정렬 값이 정렬 목록에 존재하지 않습니다."),
    NEGATIVE_LIMIT(false, 2010, "입력된 limit 값이 음수입니다."),
    EXCESS_LIMIT(false, 2011, "입력된 limit 값이 최대 허용치를 초과했습니다."),
    NO_POSTS_FOUND(false, 2020, "조회된 게시글 목록이 없습니다."),
    NO_SEARCH_QUERY(false, 2030, "검색어가 입력되지 않았습니다. 검색어를 입력해주세요."),
    SHORT_SEARCH_QUERY(false, 2031, "검색어의 길이가 짧습니다."),
    LONG_SEARCH_QUERY(false, 2032, "검색어의 길이가 깁니다."),
    MIX_SEARCH_QUERY(false, 2033, "제목 검색과 재료 검색을 혼용할 수 없습니다."),

    //상대프로필
    NOT_EXIST_USER(false,5000,"존재하는 유저가 아닙니다."),


    // 채팅
    NOT_EXIST_CHATROOM_LIST(false, 4001, "존재하는 채팅방 목록이 없습니다."),
    NOT_EXIST_CHATROOM(false, 4002, "존재하는 채팅방이 없습니다."),
    NOT_EXIST_CHAT_USER(false, 4003, "해당 유저가 없습니다."),
    NOT_EXIST_IN_CHATROOM(false, 4004, "해당 유저가 채팅방에 입장해 있지 않습니다."),
    CANT_ENTER_CHATROOM(false, 4010, "신고로 인해 채팅방에 입장할 수 없습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
