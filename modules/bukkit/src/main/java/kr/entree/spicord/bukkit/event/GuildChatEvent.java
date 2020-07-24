package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.discord.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class GuildChatEvent extends GuildMemberEvent implements MessageProvider, ChannelProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Long channel;
    private final Message message;

    public GuildChatEvent(Discord discord, Guild guild, Member member, Long channel, Message message) {
        super(discord, guild, member);
        this.channel = channel;
        this.message = message;
    }

    @SuppressWarnings("ConstantConditions")
    public static GuildChatEvent from(Discord discord, GuildMessageReceivedEvent e) {
        Validate.isTrue(!e.isWebhookMessage());
        return new GuildChatEvent(
                discord,
                Guild.of(e.getGuild()),
                Member.of(e.getMember()),
                e.getChannel().getIdLong(),
                Message.of(e.getMessage())
        );
    }

    public User getAuthor() {
        return getMessage().getAuthor();
    }

    @Override
    public Long getChannel() {
        return channel;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
