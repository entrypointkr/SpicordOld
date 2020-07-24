package kr.entree.spicord.discord.exception;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NoUserFoundException extends RuntimeException {
    @Getter
    private final Object userId;

    public NoUserFoundException(Object userId) {
        this.userId = userId;
    }

    public NoUserFoundException(String message, Object userId) {
        super(message);
        this.userId = userId;
    }

    public NoUserFoundException(String message, Throwable cause, Object userId) {
        super(message, cause);
        this.userId = userId;
    }

    public NoUserFoundException(Throwable cause, Object userId) {
        super(cause);
        this.userId = userId;
    }

    public NoUserFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object userId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.userId = userId;
    }
}
