package kr.entree.spicord.discord.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class TextChannelSupplier implements ChannelSupplier<TextChannel> {
    private final String channelid;

    private TextChannelSupplier(String channelid) {
        this.channelid = channelid;
    }

    @Override
    public void get(JDA jda, Consumer<TextChannel> consumer) {
        TextChannel channel = jda.getTextChannelById(channelid);
        if (channel == null) {
            throw new IllegalArgumentException("Unknown channel id: " + channelid);
        }
        consumer.accept(channel);
    }

    public static TextChannelSupplier of(String channelid) {
        return new TextChannelSupplier(channelid);
    }
}
