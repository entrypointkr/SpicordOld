package kr.entree.spicord.bukkit.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import kr.entree.spicord.property.config.ConfigOption;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class WebMessage implements MessageChannelHandler {
    private final WebhookManager manager;
    private final WebhookMessage message;
    private final ConfigOption<Number> webhookId;
    private final Consumer<Throwable> failure;

    public WebMessage(WebhookManager manager, WebhookMessage message, ConfigOption<Number> webhookId, Consumer<Throwable> failure) {
        this.manager = manager;
        this.message = message;
        this.webhookId = webhookId;
        this.failure = failure;
    }

    private void sendMessage(WebhookClient client) {
        webhookId.set(client.getId());
        client.send(message).whenComplete((msg, throwable) -> {
            if (throwable != null) {
                Spicord.log(throwable);
                failure.accept(throwable);
            }
        });
    }

    private void sendMessage(TextChannel channel) {
        long webhookId = this.webhookId.get().longValue();
        manager.getClient(channel, webhookId, this::sendMessage, failure);
    }

    @Override
    public void handle(MessageChannel channel) {
        if (channel instanceof TextChannel) {
            sendMessage((TextChannel) channel);
        }
    }
}
