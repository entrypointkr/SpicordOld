package kr.entree.spicord.discord.task.channel;

import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.EmptyHandler;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-30
 */
public class ChannelHandlerBuilder<T extends MessageChannel> {
    private ChannelSupplier<T> supplier;
    private MessageChannelHandler<T> handler;

    public ChannelHandlerBuilder<T> channel(ChannelSupplier<T> supplier) {
        this.supplier = supplier;
        return this;
    }

    public ChannelHandlerBuilder<T> message(MessageChannelHandler<T> handler) {
        this.handler = handler;
        return this;
    }

    public ChannelHandlerBuilder<T> message(SpicordConfig config, String key, Parameter parameter) {
        return message(config.getMessage(key, parameter));
    }

    public ChannelHandlerBuilder<T> message(SpicordConfig config, String key) {
        return message(config, key, new Parameter());
    }

    @NotNull
    public JDAHandler build() {
        if (supplier == null || handler == null) {
            return EmptyHandler.INSTANCE;
        }
        return new ChannelTask<>(supplier, handler);
    }

    public void queue(Discord discord) {
        discord.addTask(build());
    }
}
