package kr.entree.spicord.discord.exception;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NoGuildFoundException extends RuntimeException {
    @Getter
    private final long guildId;

    public NoGuildFoundException(long guildId) {
        this.guildId = guildId;
    }

    public NoGuildFoundException(String message, long guildId) {
        super(message);
        this.guildId = guildId;
    }

    public NoGuildFoundException(String message, Throwable cause, long guildId) {
        super(message, cause);
        this.guildId = guildId;
    }

    public NoGuildFoundException(Throwable cause, long guildId) {
        super(cause);
        this.guildId = guildId;
    }

    public NoGuildFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, long guildId) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.guildId = guildId;
    }
}
