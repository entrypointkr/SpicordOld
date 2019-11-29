package kr.entree.spicord.discord.supplier;

import kr.entree.spicord.discord.ChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class PrivateChannelOpener implements ChannelSupplier<PrivateChannel> {
    private final long userId;

    public PrivateChannelOpener(long userId) {
        this.userId = userId;
    }

    public PrivateChannelOpener(User user) {
        this(user.getIdLong());
    }

    public PrivateChannelOpener(Member member) {
        this(member.getUser());
    }

    @Override
    public void get(JDA jda, Consumer<PrivateChannel> consumer) {
        User user = jda.getUserById(userId);
        if (user != null) {
            user.openPrivateChannel().queue(consumer);
        } else {
            throw new IllegalArgumentException("Unknown user id: " + userId);
        }
    }
}
