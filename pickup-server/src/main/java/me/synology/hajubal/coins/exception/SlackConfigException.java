package me.synology.hajubal.coins.exception;

public class SlackConfigException extends RuntimeException {
    public SlackConfigException() {
        super();
    }

    public SlackConfigException(String message) {
        super(message);
    }

    public SlackConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public SlackConfigException(Throwable cause) {
        super(cause);
    }

    protected SlackConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
