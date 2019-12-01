package kr.entree.spicord.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class WebhookManager {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private Webhook cachedWebhook = null;

    public void getClient(TextChannel channel, Consumer<WebhookClient> receiver) {
        createWebhook(channel, webhook ->
                receiver.accept(new WebhookClientBuilder(webhook.getUrl())
                        .setExecutorService(executor)
                        .build()));
    }

    private void createWebhook(TextChannel channel, Consumer<Webhook> receiver) {
        channel.createWebhook("Spicord").queue(receiver);
    }

    public void clearCache() {
        cachedWebhook = null;
    }

    public void getWebhook(TextChannel channel, long webhookId, Consumer<Webhook> receiver) {
        if (cachedWebhook != null) {
            receiver.accept(cachedWebhook);
        } else {
            if (webhookId >= 0) {
                channel.retrieveWebhooks().queue(webhooks -> {
                    for (Webhook webhook : webhooks) {
                        if (webhook.getIdLong() == webhookId) {
                            cachedWebhook = webhook;
                            receiver.accept(webhook);
                            return;
                        }
                    }
                    createWebhook(channel, receiver);
                });
            } else {
                createWebhook(channel, receiver);
            }
        }
    }

    public boolean await() {
        try {
            return executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
