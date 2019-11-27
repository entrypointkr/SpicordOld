package kr.entree.spicord.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import net.dv8tion.jda.api.entities.Webhook;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class WebhookData {
    private final long id;
    private final String token;

    private WebhookData(long id, String token) {
        this.id = id;
        this.token = token;
    }

    public static WebhookData of(long id, String token) {
        return new WebhookData(id, token);
    }

    public static WebhookData of(Webhook webhook) {
        return of(webhook.getIdLong(), webhook.getToken());
    }

    public WebhookClientBuilder builder() {
        return new WebhookClientBuilder(id, token);
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
