package kr.entree.spicord.discord.task.channel;

import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.JDATask;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import kr.entree.spicord.discord.task.channel.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.channel.supplier.TextChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class ChannelTask extends JDATask {
    private final ChannelSupplier supplier;
    private final MessageChannelHandler handler;

    public ChannelTask(ChannelSupplier supplier, MessageChannelHandler handler) {
        this.supplier = supplier;
        this.handler = handler;
    }

    private void notify(MessageChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Given channel is null!");
        }
        handler.handle(channel);
    }

    @Override
    public void handle(JDA jda) {
        supplier.get(jda, this::notify);
    }

    public static ChannelTask ofPrivate(Object userId, MessageChannelHandler handler) {
        return new ChannelTask(
                PrivateChannelOpener.of(userId),
                handler
        );
    }

    public static ChannelTask ofText(Object channelId, MessageChannelHandler handler) {
        return new ChannelTask(
                TextChannelSupplier.of(channelId),
                handler
        );
    }
}
