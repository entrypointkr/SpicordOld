package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class GuildJoinEvent extends GuildMemberEvent {
    private static final HandlerList handlers = new HandlerList();

    public GuildJoinEvent(Guild guild, Member member) {
        super(guild, member);
    }

    public static GuildJoinEvent from(GuildMemberJoinEvent e) {
        return new GuildJoinEvent(
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
