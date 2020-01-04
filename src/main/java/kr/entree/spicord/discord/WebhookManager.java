package kr.entree.spicord.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import kr.entree.spicord.Spicord;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class WebhookManager {
    private final ScheduledExecutorService executor;
    private Webhook cachedWebhook = null;

    public WebhookManager(Spicord spicord) {
        this.executor = Executors.newScheduledThreadPool(
                2,
                new SpicordThreadFactory(Executors.defaultThreadFactory(), spicord)
        );
    }

    private WebhookClient createClient(Webhook webhook) {
        return new WebhookClientBuilder(webhook.getUrl())
                .setExecutorService(executor)
                .build();
    }

    public void getClient(TextChannel channel, long webhookId, Consumer<WebhookClient> receiver, Consumer<Throwable> failure) {
        getWebhook(channel, webhookId, webhook -> receiver.accept(createClient(webhook)), failure);
    }

    private void createWebhook(TextChannel channel, Consumer<Webhook> receiver, @Nullable Consumer<Throwable> failure) {
        WebhookAction action = channel.createWebhook("Spicord");
        if (failure != null) {
            action.queue(receiver, failure);
        } else {
            action.queue(receiver);
        }
    }

    public void clearCache() {
        cachedWebhook = null;
    }

    public void getWebhook(TextChannel channel, long webhookId, Consumer<Webhook> receiver, Consumer<Throwable> failure) {
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
                    createWebhook(channel, receiver, failure);
                });
            } else {
                createWebhook(channel, receiver, failure);
            }
        }
    }

    private boolean await() {
        return false;
    }

    public boolean stop() {
        executor.shutdownNow();
        return await();
    }
}
