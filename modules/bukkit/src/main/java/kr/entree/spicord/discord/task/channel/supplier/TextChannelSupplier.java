package kr.entree.spicord.discord.task.channel.supplier;

import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.exception.NoChannelFoundException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class TextChannelSupplier implements ChannelSupplier {
    private final Supplier<Object> supplier;

    private TextChannelSupplier(Supplier<Object> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void get(JDA jda, Consumer<MessageChannel> consumer) {
        Object id = supplier.get();
        TextChannel channel = id instanceof Number
                ? jda.getTextChannelById(((Number) id).longValue())
                : jda.getTextChannelById(id.toString());
        if (channel == null) {
            throw new NoChannelFoundException(id);
        }
        consumer.accept(channel);
    }

    public static TextChannelSupplier of(Supplier<Object> supplier) {
        return new TextChannelSupplier(supplier);
    }

    public static TextChannelSupplier ofConfigurized(SpicordConfig config, String channel) {
        return of(() -> config.remapChannel(channel));
    }

    public static TextChannelSupplier of(Object channelid) {
        return of(() -> channelid);
    }
}
