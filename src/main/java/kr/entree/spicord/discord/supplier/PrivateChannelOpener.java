package kr.entree.spicord.discord.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class PrivateChannelOpener implements ChannelSupplier<PrivateChannel> {
    private final LongSupplier supplier;

    public PrivateChannelOpener(LongSupplier supplier) {
        this.supplier = supplier;
    }

    public static PrivateChannelOpener of(LongSupplier supplier) {
        return new PrivateChannelOpener(supplier);
    }

    public static PrivateChannelOpener of(Long userId) {
        return of(() -> userId);
    }

    public static PrivateChannelOpener of(User user) {
        return of(user.getIdLong());
    }

    @Override
    public void get(JDA jda, Consumer<PrivateChannel> consumer) {
        long id = supplier.getAsLong();
        User user = jda.getUserById(id);
        if (user != null) {
            user.openPrivateChannel().queue(consumer);
        } else {
            throw new IllegalArgumentException("Unknown user id: " + id);
        }
    }
}
