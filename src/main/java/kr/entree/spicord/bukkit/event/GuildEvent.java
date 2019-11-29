package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.GuildProvider;
import kr.entree.spicord.discord.Discord;
import org.bukkit.event.Event;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public abstract class GuildEvent extends DiscordEvent implements GuildProvider {
    private final Guild guild;

    public GuildEvent(Discord discord, Guild guild) {
        super(discord);
        this.guild = guild;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }
}
