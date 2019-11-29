package kr.entree.spicord.discord.handler;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class CombinedMessage<T extends MessageChannel> implements MessageChannelHandler<T> {
    private final Collection<MessageChannelHandler<T>> handlers;

    private CombinedMessage(Collection<MessageChannelHandler<T>> handlers) {
        this.handlers = handlers;
    }

    public static <T extends MessageChannel> CombinedMessage<T> of(Collection<MessageChannelHandler<T>> handlers) {
        return new CombinedMessage<>(handlers);
    }

    public static <T extends MessageChannel> CombinedMessage<T> ofList() {
        return of(new ArrayList<>());
    }

    @SafeVarargs
    public final CombinedMessage<T> add(MessageChannelHandler<T>... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
        return this;
    }

    @Override
    public void handle(T channel) {
        for (MessageChannelHandler<T> handler : handlers) {
            handler.handle(channel);
        }
    }
}
