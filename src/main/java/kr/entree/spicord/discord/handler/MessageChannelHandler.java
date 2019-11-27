package kr.entree.spicord.discord.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public interface MessageChannelHandler<T extends MessageChannel> {
    void handle(T channel);
}
