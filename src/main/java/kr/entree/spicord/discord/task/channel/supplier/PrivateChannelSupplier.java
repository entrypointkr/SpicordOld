package kr.entree.spicord.discord.task.channel.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.exception.NoChannelFoundException;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PrivateChannelSupplier implements ChannelSupplier<PrivateChannel> {
    private final Supplier<String> supplier;

    private PrivateChannelSupplier(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    public static PrivateChannelSupplier of(Supplier<String> supplier) {
        return new PrivateChannelSupplier(supplier);
    }

    public static PrivateChannelSupplier of(String id) {
        return of(() -> id);
    }

    @Override
    public void get(JDA jda, Consumer<PrivateChannel> consumer) {
        val id = supplier.get();
        val channel = jda.getPrivateChannelById(id);
        if (channel == null) {
            throw new NoChannelFoundException(id);
        }
        consumer.accept(jda.getPrivateChannelById(id));
    }
}
