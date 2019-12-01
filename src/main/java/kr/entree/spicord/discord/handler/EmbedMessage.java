package kr.entree.spicord.discord.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class EmbedMessage<T extends MessageChannel> extends RestActor<T, Message> {
    private final MessageEmbed message;

    public EmbedMessage(MessageEmbed message) {
        this.message = message;
    }

    @Override
    protected RestAction<Message> action(T channel) {
        return channel.sendMessage(message);
    }
}
