package kr.entree.spicord.discord.exception;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NoUserFoundException extends RuntimeException {
    @Getter
    private final long userId;

    public NoUserFoundException(long userId) {
        this.userId = userId;
    }

    public NoUserFoundException(String message, long userId) {
        super(message);
        this.userId = userId;
    }

    public NoUserFoundException(String message, Throwable cause, long userId) {
        super(message, cause);
        this.userId = userId;
    }

    public NoUserFoundException(Throwable cause, long userId) {
        super(cause);
        this.userId = userId;
    }

    public NoUserFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, long userId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.userId = userId;
    }
}
