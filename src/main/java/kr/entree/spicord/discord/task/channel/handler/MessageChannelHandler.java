package kr.entree.spicord.discord.task.channel.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public interface MessageChannelHandler {
    void handle(MessageChannel channel);
}
