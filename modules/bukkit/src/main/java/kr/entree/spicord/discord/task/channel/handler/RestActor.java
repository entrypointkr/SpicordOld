package kr.entree.spicord.discord.task.channel.handler;

import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public abstract class RestActor implements MessageChannelHandler {
    @Setter
    private Consumer<Object> success;
    @Setter
    private Consumer<Throwable> fail;

    @Override
    public final void handle(MessageChannel channel) {
        action(channel).queue(success, fail);
    }

    protected abstract RestAction<?> action(MessageChannel channel);
}