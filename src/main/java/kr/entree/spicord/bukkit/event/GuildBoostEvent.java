package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.Member;
import kr.entree.spicord.discord.Discord;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class GuildBoostEvent extends GuildMemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final OffsetDateTime oldTime;
    private final OffsetDateTime newTime;

    public GuildBoostEvent(Discord discord, Guild guild, Member member, OffsetDateTime oldTime, OffsetDateTime newTime) {
        super(discord, guild, member);
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    public static GuildBoostEvent from(Discord discord, GuildMemberUpdateBoostTimeEvent e) {
        return new GuildBoostEvent(
                discord,
                Guild.of(e.getGuild()),
                Member.of(e.getMember()),
                e.getOldTimeBoosted(),
                e.getNewTimeBoosted()
        );
    }

    public boolean isBoosted() {
        return newTime != null;
    }

    public Optional<OffsetDateTime> getOldTime() {
        return Optional.ofNullable(oldTime);
    }

    public Optional<OffsetDateTime> getNewTime() {
        return Optional.ofNullable(newTime);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
