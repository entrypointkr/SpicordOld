package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.GuildProvider;
import org.bukkit.event.Event;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public abstract class GuildEvent extends Event implements GuildProvider {
    private final Guild guild;

    public GuildEvent(Guild guild) {
        this.guild = guild;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }
}
