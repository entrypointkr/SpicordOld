package kr.entree.spicord.discord.exception;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class NoChannelFoundException extends RuntimeException {
    @Getter
    private final String channelId;

    public NoChannelFoundException(String channelId) {
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, String channelId) {
        super(message);
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, Throwable cause, String channelId) {
        super(message, cause);
        this.channelId = channelId;
    }

    public NoChannelFoundException(Throwable cause, String channelId) {
        super(cause);
        this.channelId = channelId;
    }

    public NoChannelFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String channelId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.channelId = channelId;
    }
}
