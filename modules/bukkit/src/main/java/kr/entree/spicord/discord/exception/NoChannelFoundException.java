package kr.entree.spicord.discord.exception;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class NoChannelFoundException extends RuntimeException {
    @Getter
    private final Object channelId;

    public NoChannelFoundException(Object channelId) {
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, Object channelId) {
        super(message);
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, Throwable cause, Object channelId) {
        super(message, cause);
        this.channelId = channelId;
    }

    public NoChannelFoundException(Throwable cause, Object channelId) {
        super(cause);
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object channelId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.channelId = channelId;
    }
}
