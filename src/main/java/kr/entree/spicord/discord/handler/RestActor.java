package kr.entree.spicord.discord.handler;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public abstract class RestActor<T extends MessageChannel> implements MessageChannelHandler<T> {
    @Override
    public final void handle(T channel) {
        action(channel).queue();
    }

    protected abstract RestAction<?> action(T channel);
}
