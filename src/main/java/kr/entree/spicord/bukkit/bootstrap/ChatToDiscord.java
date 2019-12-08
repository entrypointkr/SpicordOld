package kr.entree.spicord.bukkit.bootstrap;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.discord.WebMessage;
import kr.entree.spicord.config.DataStorage;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

import static kr.entree.spicord.config.SpicordConfig.featureKey;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class ChatToDiscord implements Listener {
    private final Plugin plugin;
    private final Discord discord;
    private final SpicordConfig config;
    private final DataStorage storage;
    private final WebhookManager manager;
    private final StringBuilder builder = new StringBuilder();
    private Player last = null;
    private BukkitTask task = null;
    private long lastFlushTime = 0;

    public ChatToDiscord(Plugin plugin, Discord discord, SpicordConfig config, DataStorage storage, WebhookManager manager) {
        this.plugin = plugin;
        this.discord = discord;
        this.config = config;
        this.storage = storage;
        this.manager = manager;
    }

    public static String createAvatarUrl(Object uuid) {
        return String.format("https://crafatar.com/avatars/%s?overlay=true", uuid);
    }

    private void sendPlainMessage(Player player, String message) {
        val parameter = new Parameter().put(player)
                .put("%message%", message);
        discord.addTask(config.getSendMessage("player-chat", parameter));
    }

    private void failedWebhook(Throwable throwable, Player player, String message) {
        plugin.getLogger().log(Level.SEVERE, throwable, () ->
                "Failed creating webhook. This feature will be disabled.");
        config.getFakeProfilePlayerChat().set(false);
        sendPlainMessage(player, message);
    }

    private void queueNow(Player player, String message) {
        if (message.isEmpty()) {
            return;
        }
        if (config.getFakeProfilePlayerChat().isTrue()) {
            val builder = new WebhookMessageBuilder()
                    .setUsername(player.getName())
                    .setAvatarUrl(createAvatarUrl(player.getUniqueId()))
                    .setContent(message);
            val sendMessage = new WebMessage(
                    manager,
                    builder.build(),
                    storage.getPlayerChatWebhookId(),
                    throwable -> failedWebhook(throwable, player, message)
            );
            discord.addTask(config.getSendMessage("player-chat", sendMessage));
        } else {
            sendPlainMessage(player, message);
        }
    }

    private void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void flushQueue() {
        queueNow(last, builder.toString());
        last = null;
        builder.setLength(0);
        cancelTask();
        lastFlushTime = System.currentTimeMillis();
    }

    private void queueSlowly(Player player, String message) {
        if (player.equals(last)) {
            builder.append('\n');
        } else {
            if (last != null) {
                flushQueue();
            }
            last = player;
        }
        val timeDiff = System.currentTimeMillis() - lastFlushTime;
        builder.append(message);
        if (timeDiff >= 3000) {
            flushQueue();
        } else {
            cancelTask();
            task = Bukkit.getScheduler().runTaskLater(Spicord.get(), this::flushQueue, 3L * 20L);
        }
    }

    private void chat(Player player, String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            String uncolored = ChatColor.stripColor(message);
            if (config.isSlowModePlayerChat()) {
                queueSlowly(player, uncolored);
            } else {
                queueNow(player, uncolored);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled() && isIgnoreCancelled()) {
            return;
        }
        chat(e.getPlayer(), e.getMessage());
    }

    private boolean isJoinQuitEnabled() {
        return config.getBoolean(featureKey("player-chat.join-quit"), true);
    }

    private boolean isIgnoreCancelled() {
        return config.getBoolean(featureKey("player-chat.ignore-cancel"), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (isJoinQuitEnabled()) {
            chat(e.getPlayer(), e.getQuitMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (isJoinQuitEnabled()) {
            chat(e.getPlayer(), e.getJoinMessage());
        }
    }
}
