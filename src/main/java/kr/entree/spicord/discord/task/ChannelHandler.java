package kr.entree.spicord.discord.task;

import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import kr.entree.spicord.discord.handler.PlainMessage;
import kr.entree.spicord.discord.supplier.PrivateChannelSupplier;
import kr.entree.spicord.discord.supplier.TextChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class ChannelHandler<T extends MessageChannel> implements JDAHandler {
    private final ChannelSupplier<T> supplier;
    private final MessageChannelHandler<T> handler;

    private ChannelHandler(ChannelSupplier<T> supplier, MessageChannelHandler<T> handler) {
        this.supplier = supplier;
        this.handler = handler;
    }

    public static <T extends MessageChannel> ChannelHandler<T> of(ChannelSupplier<T> channelSupplier, MessageChannelHandler<T> handler) {
        return new ChannelHandler<>(channelSupplier, handler);
    }

    public static ChannelHandler<TextChannel> ofText(String channel, MessageChannelHandler<TextChannel> handler) {
        return of(TextChannelSupplier.of(channel), handler);
    }

    public static ChannelHandler<TextChannel> ofText(String channel, String message) {
        return ofText(channel, new PlainMessage<>(message));
    }

    public static ChannelHandler<PrivateChannel> ofPrivate(long channel, MessageChannelHandler<PrivateChannel> handler) {
        return of(new PrivateChannelSupplier(channel), handler);
    }

    private void notify(T channel) {
        if (channel != null) {
            handler.handle(channel);
        } else {
            throw new IllegalStateException("Given channel is null!");
        }
    }

    @Override
    public void handle(JDA jda) {
        supplier.get(jda, this::notify);
    }
}
