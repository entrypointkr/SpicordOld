package kr.entree.spicord.discord.task.channel;

import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class ChannelTask<T extends MessageChannel> implements JDAHandler {
    private final ChannelSupplier<T> supplier;
    private final MessageChannelHandler<T> handler;

    public ChannelTask(ChannelSupplier<T> supplier, MessageChannelHandler<T> handler) {
        this.supplier = supplier;
        this.handler = handler;
    }

    private void notify(T channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Given channel is null!");
        }
        handler.handle(channel);
    }

    @Override
    public void handle(JDA jda) {
        supplier.get(jda, this::notify);
    }
}
