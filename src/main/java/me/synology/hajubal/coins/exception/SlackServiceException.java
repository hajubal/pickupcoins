package me.synology.hajubal.coins.exception;

public class SlackServiceException extends RuntimeException {
    public SlackServiceException() {
        super();
    }

    public SlackServiceException(String message) {
        super(message);
    }

    public SlackServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SlackServiceException(Throwable cause) {
        super(cause);
    }

    protected SlackServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
