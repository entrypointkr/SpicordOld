package kr.entree.spicord.discord.task.channel.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class EmptyMessageChannelHandler implements MessageChannelHandler {
    public static final EmptyMessageChannelHandler INSTANCE = new EmptyMessageChannelHandler();

    private EmptyMessageChannelHandler() {
    }

    @Override
    public void handle(MessageChannel channel) {

    }
}
