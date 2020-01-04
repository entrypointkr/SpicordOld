package kr.entree.spicord.discord.task.channel.handler;

import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class PlainMessage extends RestActor {
    @Getter
    private final Object message;

    public PlainMessage(@NotNull Object message) {
        this.message = message;
    }

    @Override
    protected MessageAction action(MessageChannel channel) {
        return channel.sendMessage(message.toString());
    }
}
