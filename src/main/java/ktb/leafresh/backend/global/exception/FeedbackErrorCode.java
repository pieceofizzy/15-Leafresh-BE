package ktb.leafresh.backend.global.exception;

import org.springframework.http.HttpStatus;

public enum FeedbackErrorCode implements BaseErrorCode {

    MISSING_MEMBER_ID(HttpStatus.BAD_REQUEST, "memberId는 필수입니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "요청 값이 유효하지 않습니다. 챌린지 데이터가 모두 포함되어야 합니다."),
    NO_CHALLENGE_ACTIVITY(HttpStatus.UNPROCESSABLE_ENTITY, "피드백 생성을 위한 활동 데이터가 부족합니다. 최소 1개의 챌린지 참여가 필요합니다."),
    FEEDBACK_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 피드백 생성을 완료하지 못했습니다. 잠시 후 다시 시도해주세요."),
    NO_ACTIVITY_IN_PAST_WEEK(HttpStatus.BAD_REQUEST, "최근 일주일간 활동 기록이 부족하여 피드백을 제공할 수 없습니다."),
    ALREADY_REQUESTED_TODAY(HttpStatus.BAD_REQUEST, "이미 오늘 피드백을 받았습니다.");

    private final HttpStatus status;
    private final String message;

    FeedbackErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
