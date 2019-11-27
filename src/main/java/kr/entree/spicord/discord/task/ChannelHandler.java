package kr.entree.spicord.discord.task;

import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import kr.entree.spicord.discord.handler.PlainMessage;
import kr.entree.spicord.discord.supplier.TextChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
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

    public static ChannelHandler<TextChannel> ofText(SpicordConfig config, String channel, MessageChannelHandler<TextChannel> handler) {
        return of(TextChannelSupplier.ofConfigurized(config, channel), handler);
    }

    public static ChannelHandler<TextChannel> ofText(SpicordConfig config, String channel, String message) {
        return ofText(config, channel, new PlainMessage<>(message));
    }

    @Override
    public void handle(JDA jda) {
        supplier.get(jda, handler::handle);
    }
}
