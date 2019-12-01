package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.Member;
import kr.entree.spicord.discord.Discord;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class GuildMemberNamingEvent extends GuildMemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final String from;
    private final String to;

    public GuildMemberNamingEvent(Discord discord, Guild guild, Member member, String from, String to) {
        super(discord, guild, member);
        this.from = from;
        this.to = to;
    }

    public static GuildMemberNamingEvent from(Discord discord, GuildMemberUpdateNicknameEvent e) {
        return new GuildMemberNamingEvent(
                discord,
                Guild.of(e.getGuild()),
                Member.of(e.getMember()),
                e.getOldNickname(),
                e.getNewNickname()
        );
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
