package kr.entree.spicord.bukkit;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.discord.BukkitMessage;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.GuildJoinEvent;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.ChannelHandler;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import static kr.entree.spicord.config.SpicordConfig.featureKey;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class SpicordOperator implements Listener {
    private final Plugin plugin;
    private final Discord discord;
    private final SpicordConfig config;
    private final WebhookManager webhookManager;
    private final StringBuilder builder = new StringBuilder();
    private Player last = null;
    private BukkitTask task = null;
    private long lastFlushTime = 0;

    public SpicordOperator(Plugin plugin, Discord discord, SpicordConfig config, WebhookManager webhookManager) {
        this.plugin = plugin;
        this.discord = discord;
        this.config = config;
        this.webhookManager = webhookManager;
    }

    private void queueNow(Player player, String message) {
        if (config.isFakeProfilePlayerChat()) {
            val sendMessage = new BukkitMessage(webhookManager, player, message);
            discord.addTask(config.getSendMessage("player-chat", sendMessage));
        } else {
            val parameter = new Parameter().put(player)
                    .put("%message%", message);
            discord.addTask(config.getSendMessage("player-chat", parameter));
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
            if (config.isSlowModePlayerChat()) {
                queueSlowly(player, message);
            } else {
                queueNow(player, message);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        chat(e.getPlayer(), e.getMessage());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        webhookManager.remove(e.getPlayer());
        chat(e.getPlayer(), e.getQuitMessage());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        chat(e.getPlayer(), e.getJoinMessage());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        discord.addTask(config.getSendMessage(
                "player-kick",
                new Parameter().put(player)
                        .put("%reason%", e.getReason())
        ));
    }

    @EventHandler
    public void onGuildJoin(GuildJoinEvent e) {
        if (!config.isEnabled(featureKey("welcome"))) {
            return;
        }
        if (e.getUser().isBot()) {
            return;
        }
        val parameter = new Parameter().put(e.getUser());
        e.getDiscord().addTask(new ChannelHandler<>(
                PrivateChannelOpener.of(e.getUser().getId()),
                config.getMessage("welcome", parameter)
        ));
    }

    @EventHandler
    public void onGuildChat(GuildChatEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        String baseKey = featureKey("discord-chat");
        if (!config.isEnabled(baseKey)) {
            return;
        }
        val channels = config.getChannelIds(baseKey + ".channel", true);
        if (!channels.contains(e.getChannel().toString())) {
            return;
        }
        val discordChat = config.getDiscordChat();
        Bukkit.getScheduler().runTask(plugin, () ->
                discordChat.send(e.getMember().getName(), e.getMessage().getContents()));
    }
}
