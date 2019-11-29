package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.structure.MessageProvider;
import kr.entree.spicord.bukkit.structure.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PrivateChatEvent extends Event implements MessageProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Message message;

    public PrivateChatEvent(Message message) {
        this.message = message;
    }

    public static PrivateChatEvent from(PrivateMessageReceivedEvent e) {
        return new PrivateChatEvent(Message.of(e.getMessage()));
    }

    public User getAuthor() {
        return getMessage().getAuthor();
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
