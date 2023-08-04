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
    POST_USERS_EXISTS_ID(false,HttpStatus.BAD_REQUEST.value(),"중복된 아이디입니다"),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    FOLLOW_USER_ERROR(false,HttpStatus.BAD_REQUEST.value(),"유저 팔로우에 실패하였습니다."),
    FOLLOW_USER_ALREADY(false,HttpStatus.BAD_REQUEST.value(),"이미 팔로우 하셨습니다."),

    //[DELETE]
    DELETE_USER_ERROR(false, HttpStatus.BAD_REQUEST.value(), "팔로우 취소가 실패하였습니다."),

    /**
     * 50 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),
    ERRRRRRR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),
//<<<<<<< HEAD

    /**
     * 1000: 유저 관련 오류
     */
    NEEDED_EMIAL_INPUT(false,1000, "이메일을 입력해주세요."),
    MODIFY_FAIL_PWD(false,1001,"비밀번호 재설정에 실패하였습니다."),







//=======
//>>>>>>> 7130bb5f63f8b15a02d00cbd75fe6c3f1791faa5

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

    //게시글
    NOT_EXIST_POST_IDX(false,3000,"존재하는 게시글idx가 아닙니다."),
    OVER_LENGTH(false, 3001, "내용이 정해진 길이를 초과했습니다."),
    WRONG_CATEGORY(false, 3002, "존재하지 않는 카테고리입니다."),
    PERMISSION_DENIED(false, 3003, "작업 권한이 없는 유저입니다."),
    OMITTED_PARAMETER(false,3004,"파라미터가 누락되었습니다."),
    ALREADY_CALCULATED(false,3005,"이미 정산 완료된 공동구매입니다."),
    NOT_EXIST_COMMENT_IDX(false,3006,"존재하지 않는 댓글 idx입니다."),
    SELF_REPORT(false,3007,"자기 자신을 신고할 수 없습니다."),

    //상대프로필
    NOT_EXIST_USER(false,5000,"존재하는 유저가 아닙니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}