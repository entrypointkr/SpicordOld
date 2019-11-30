package kr.entree.spicord.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import lombok.val;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class WebhookManager {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final Map<UUID, WebhookClient> clientByPlayer = new HashMap<>();
    private final Plugin plugin;

    public WebhookManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public WebhookClient compute(Player player, Function<UUID, WebhookClient> webhookUrlCreator) {
        return clientByPlayer.computeIfAbsent(player.getUniqueId(), webhookUrlCreator);
    }

    public WebhookClient compute(Player player, TextChannel channel) {
        return compute(player, k -> createClient(channel, player));
    }

    public WebhookClient remove(Player player) {
        return clientByPlayer.remove(player.getUniqueId());
    }

    public WebhookClient createClient(TextChannel channel, Player player) {
        for (Webhook webhook : channel.retrieveWebhooks().complete()) {
            if (webhook.getName().equalsIgnoreCase(player.getName())) {
                createIcon(player).ifPresent(icon ->
                        webhook.getManager().setAvatar(icon).queue());
                return createClient(webhook);
            }
        }
        val action = channel.createWebhook(player.getName());
        createIcon(player).ifPresent(icon -> action.setAvatar(icon).queue());
        val webhook = action.complete();
        return createClient(webhook);
    }

    public WebhookClient createClient(Webhook webhook) {
        return new WebhookClientBuilder(webhook.getUrl())
                .setExecutorService(executor)
                .build();
    }

    public Optional<Icon> createIcon(Player player) {
        try {
            val url = new URL(String.format("https://crafatar.com/avatars/%s?overlay=true", player.getUniqueId()));
            return Optional.of(Icon.from(new BufferedInputStream(url.openStream())));
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, e, () -> "Failed getting icon");
        }
        return Optional.empty();
    }

    public boolean await() {
        CountDownLatch latch = new CountDownLatch(1);
        executor.submit(latch::countDown);
        try {
            return latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
