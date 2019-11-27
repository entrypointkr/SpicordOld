package kr.entree.spicord.discord.exception;

import lombok.Getter;

import java.util.Collection;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class NoChannelFoundException extends RuntimeException {
    @Getter
    private final Collection<String> channelIds;

    public NoChannelFoundException(Collection<String> channelIds) {
        this.channelIds = channelIds;
    }

    public NoChannelFoundException(String message, Collection<String> channelIds) {
        super(message);
        this.channelIds = channelIds;
    }

    public NoChannelFoundException(String message, Throwable cause, Collection<String> channelIds) {
        super(message, cause);
        this.channelIds = channelIds;
    }

    public NoChannelFoundException(Throwable cause, Collection<String> channelIds) {
        super(cause);
        this.channelIds = channelIds;
    }

    public NoChannelFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Collection<String> channelIds) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.channelIds = channelIds;
    }
}
