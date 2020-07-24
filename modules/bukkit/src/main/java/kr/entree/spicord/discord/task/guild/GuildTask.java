package kr.entree.spicord.discord.task.guild;

import kr.entree.spicord.discord.JDATask;
import kr.entree.spicord.discord.exception.NoGuildFoundException;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.function.BiConsumer;
import java.util.function.LongSupplier;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class GuildTask extends JDATask {
    private final LongSupplier guildId;
    private final BiConsumer<JDA, Guild> handler;

    public GuildTask(LongSupplier guildId, BiConsumer<JDA, Guild> handler) {
        this.guildId = guildId;
        this.handler = handler;
    }

    public GuildTask(long guildId, BiConsumer<JDA, Guild> handler) {
        this(() -> guildId, handler);
    }

    @Override
    public void handle(JDA jda) {
        val guildId = this.guildId.getAsLong();
        val guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new NoGuildFoundException(guildId);
        }
        handler.accept(jda, guild);
    }
}
