package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.discord.Discord;
import org.bukkit.event.Event;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public abstract class DiscordEvent extends Event {
    private final Discord discord;

    public DiscordEvent(Discord discord) {
        this.discord = discord;
    }

    public Discord getDiscord() {
        return discord;
    }
}
