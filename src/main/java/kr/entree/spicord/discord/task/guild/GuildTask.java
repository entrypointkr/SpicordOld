package kr.entree.spicord.discord.task.guild;

import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.exception.NoGuildFoundException;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class GuildTask implements JDAHandler {
    private final long guildId;
    private final Consumer<Guild> handler;

    public GuildTask(long guildId, Consumer<Guild> handler) {
        this.guildId = guildId;
        this.handler = handler;
    }

    @Override
    public void handle(JDA jda) {
        val guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new NoGuildFoundException(guildId);
        }
        handler.accept(guild);
    }
}
