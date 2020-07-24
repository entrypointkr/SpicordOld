package kr.entree.spicord.discord.task.channel.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.exception.NoUserFoundException;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class PrivateChannelOpener implements ChannelSupplier {
    private final Supplier<Object> supplier;

    public PrivateChannelOpener(Supplier<Object> supplier) {
        this.supplier = supplier;
    }

    public static PrivateChannelOpener of(Supplier<Object> idSupplier) {
        return new PrivateChannelOpener(idSupplier);
    }

    public static PrivateChannelOpener of(Object userId) {
        return of(() -> userId);
    }

    public static PrivateChannelOpener of(User user) {
        return of(user.getIdLong());
    }

    @Override
    public void get(JDA jda, Consumer<MessageChannel> consumer) {
        val id = supplier.get();
        User user = id instanceof Number
                ? jda.getUserById(((Number) id).longValue())
                : jda.getUserById(id.toString());
        if (user == null) {
            throw new NoUserFoundException(id);
        }
        user.openPrivateChannel().queue(consumer);
    }
}
