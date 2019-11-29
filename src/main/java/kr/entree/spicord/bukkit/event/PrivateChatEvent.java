package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.ChannelProvider;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.structure.MessageProvider;
import kr.entree.spicord.bukkit.structure.User;
import kr.entree.spicord.discord.Discord;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PrivateChatEvent extends DiscordEvent implements MessageProvider, ChannelProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Message message;
    private final Long channel;

    public PrivateChatEvent(Discord discord, Message message, Long channel) {
        super(discord);
        this.message = message;
        this.channel = channel;
    }

    public static PrivateChatEvent from(Discord discord, PrivateMessageReceivedEvent e) {
        return new PrivateChatEvent(discord, Message.of(e.getMessage()), e.getChannel().getIdLong());
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
