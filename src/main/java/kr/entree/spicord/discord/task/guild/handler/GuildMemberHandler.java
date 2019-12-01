package kr.entree.spicord.discord.task.guild.handler;

import kr.entree.spicord.discord.exception.NoUserFoundException;
import kr.entree.spicord.discord.task.guild.GuildTask;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class GuildMemberHandler implements Consumer<Guild> {
    private final long userId;
    private final BiConsumer<Guild, Member> handler;

    public GuildMemberHandler(long userId, BiConsumer<Guild, Member> handler) {
        this.userId = userId;
        this.handler = handler;
    }

    @SafeVarargs
    public static GuildTask createTask(long guildId, long userId, BiConsumer<Guild, Member>... handlers) {
        return new GuildTask(guildId, new GuildMemberHandler(
                userId,
                (guild, member) -> {
                    for (BiConsumer<Guild, Member> handler : handlers) {
                        handler.accept(guild, member);
                    }
                }
        ));
    }

    @Override
    public void accept(Guild guild) {
        val member = guild.getMemberById(userId);
        if (member == null) {
            throw new NoUserFoundException(userId);
        }
        handler.accept(guild, member);
    }
}
