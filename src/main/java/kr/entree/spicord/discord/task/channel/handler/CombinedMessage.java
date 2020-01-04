package kr.entree.spicord.discord.task.channel.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class CombinedMessage implements MessageChannelHandler {
    private final Collection<MessageChannelHandler> handlers;

    private CombinedMessage(Collection<MessageChannelHandler> handlers) {
        this.handlers = handlers;
    }

    public static <T extends MessageChannel> CombinedMessage of(Collection<MessageChannelHandler> handlers) {
        return new CombinedMessage(handlers);
    }

    public static <T extends MessageChannel> CombinedMessage ofList() {
        return of(new ArrayList<>());
    }

    public final CombinedMessage add(MessageChannelHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
        return this;
    }

    @Override
    public void handle(MessageChannel channel) {
        for (MessageChannelHandler handler : handlers) {
            handler.handle(channel);
        }
    }
}
