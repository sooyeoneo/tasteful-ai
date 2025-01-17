package com.example.tastefulai.global.error.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잠시 후 다시 시도해주세요."),
    DUPLICATE_RESOURCE(HttpStatus.BAD_REQUEST, "중복된 이메일이 존재합니다."),
    EMAIL_FORM_ERROR(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    PASSWORD_PATTERN_ERROR(HttpStatus.BAD_REQUEST, "비밀번호는 최소 8자 이상이어야 하며, 숫자, 특수문자, 대문자를 포함해야 합니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "지원하지 않는 형식의 파일입니다."),
    LARGE_FILE(HttpStatus.BAD_REQUEST, "파일 용량 크기를 초과하였습니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "잘못된 인증 정보 입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST,"올바르지 않은 형식입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"이미 사용중인 닉네임 입니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다."),
    VERIFY_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호 검증이 필요합니다."),
    PASSWORD_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "비밀번호가 변경되지 않았습니다.."),
    INVALID_SPICY_LEVEL (HttpStatus.BAD_REQUEST, "유효하지 않은 매운 정도 값입니다."),
    NO_IMAGE_TO_DELETE (HttpStatus.BAD_REQUEST, "삭제할 파일이 존재하지 않습니다."),
    ADMIN_CANNOT_REMOVE_ADMIN(HttpStatus.BAD_REQUEST, "관리자는 다른 관리자를 삭제할 수 없습니다. "),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "JSON 데이터 형식이 올바르지 않습니다."),
    CANNOT_MODIFY_DEACTIVATED_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 사용자는 권한을 변경할 수 없습니다."),
    CANNOT_CHANGE_TO_SAME_ROLE(HttpStatus.BAD_REQUEST, "이미 동일한 권한입니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인 해주세요."),
    UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호 입니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "본인이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    PASSWORD_CHANGED_RECENTLY(HttpStatus.UNAUTHORIZED, "최근 비밀번호가 변경되었습니다. 다시 시도해주세요."),
    UNAUTHORIZED_OPERATION(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 404 NOT_FOUND
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 페이지를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버 정보를 찾을 수 없습니다."),
    DELETE_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "삭제 대상 사용자를 찾을 수 없습니다."),

    // 503 SERVICE_UNAVAILABLE
    TOO_MANY_REQUESTS(HttpStatus.SERVICE_UNAVAILABLE, "오늘의 메뉴 추천 요청 횟수를 초과했습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
