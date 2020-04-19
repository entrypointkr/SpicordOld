package kr.entree.spicord.discord.task.channel.handler;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class Reaction extends RestActor {
    private final long messageId;
    private final String unicode;

    public Reaction(long messageId, String unicode) {
        this.messageId = messageId;
        this.unicode = unicode;
    }

    @Override
    protected RestAction<?> action(MessageChannel channel) {
        return channel.addReactionById(messageId, unicode);
    }
}
