package kr.entree.spicord.discord.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PrivateChannelSupplier implements ChannelSupplier<PrivateChannel> {
    private final long channelId;

    public PrivateChannelSupplier(long channelId) {
        this.channelId = channelId;
    }

    @Override
    public void get(JDA jda, Consumer<PrivateChannel> consumer) {
        consumer.accept(jda.getPrivateChannelById(channelId));
    }
}
