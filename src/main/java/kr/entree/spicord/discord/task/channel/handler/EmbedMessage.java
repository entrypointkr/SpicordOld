package kr.entree.spicord.discord.task.channel.handler;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class EmbedMessage extends RestActor {
    private final MessageEmbed message;

    public EmbedMessage(MessageEmbed message) {
        this.message = message;
    }

    @Override
    protected RestAction<?> action(MessageChannel channel) {
        return channel.sendMessage(message);
    }
}
