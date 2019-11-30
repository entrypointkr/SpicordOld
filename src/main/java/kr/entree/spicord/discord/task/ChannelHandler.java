package kr.entree.spicord.discord.task;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class ChannelHandler<T extends MessageChannel> implements JDAHandler {
    private final ChannelSupplier<T> supplier;
    private final MessageChannelHandler<T> handler;

    public ChannelHandler(ChannelSupplier<T> supplier, MessageChannelHandler<T> handler) {
        this.supplier = supplier;
        this.handler = handler;
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
        try {
            supplier.get(jda, this::notify);
        } catch (Exception ex) {
            Spicord.log(ex);
        }
    }
}
