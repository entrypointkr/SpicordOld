package kr.entree.spicord.discord.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class EmptyMessageChannelHandler<T extends MessageChannel> implements MessageChannelHandler<T> {
    @Override
    public void handle(T channel) {
        // Empty
    }
}
