package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.Guild;
import kr.entree.spicord.discord.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class GuildJoinEvent extends GuildMemberEvent {
    private static final HandlerList handlers = new HandlerList();

    public GuildJoinEvent(Discord discord, Guild guild, Member member) {
        super(discord, guild, member);
    }

    public static GuildJoinEvent from(Discord discord, GuildMemberJoinEvent e) {
        return new GuildJoinEvent(
                discord,
                Guild.of(e.getGuild()),
                Member.of(e.getMember())
        );
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
