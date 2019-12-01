package kr.entree.spicord.bukkit.discord;

import club.minnced.discord.webhook.send.WebhookMessage;
import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class WebMessage implements MessageChannelHandler<TextChannel> {
    private final WebhookManager manager;
    private final WebhookMessage message;

    public WebMessage(WebhookManager manager, WebhookMessage message) {
        this.manager = manager;
        this.message = message;
    }

    private void sendMessage(TextChannel channel) {
        manager.getClient(channel, client ->
                client.send(message).whenComplete((msg, throwable) -> {
                    if (throwable != null) {
                        manager.clearCache();
                        sendMessage(channel);
                    }
                })
        );
    }

    @Override
    public void handle(TextChannel channel) {
        sendMessage(channel);
    }
}
