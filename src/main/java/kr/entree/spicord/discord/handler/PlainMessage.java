package kr.entree.spicord.discord.handler;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class PlainMessage<T extends MessageChannel> extends RestActor<T, Message> {
    @Getter
    private final String message;

    public PlainMessage(Object message) {
        this.message = message.toString();
    }

    @Override
    protected MessageAction action(T channel) {
        return channel.sendMessage(message);
    }
}
