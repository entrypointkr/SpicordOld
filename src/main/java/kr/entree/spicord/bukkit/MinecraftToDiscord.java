package kr.entree.spicord.bukkit;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.discord.BukkitMessage;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.config.VerificationConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class MinecraftToDiscord implements Listener {
    private final Discord discord;
    private final SpicordConfig config;
    private final VerifiedMemberManager verifiedManager;
    private final WebhookManager webhookManager;

    private final StringBuilder builder = new StringBuilder();
    private Player last = null;
    private BukkitTask task = null;
    private long lastFlushTime = 0;

    public MinecraftToDiscord(Discord discord, SpicordConfig config, VerifiedMemberManager verifiedManager, WebhookManager webhookManager) {
        this.discord = discord;
        this.config = config;
        this.verifiedManager = verifiedManager;
        this.webhookManager = webhookManager;
    }

    private void queueNow(Player player, String message) {
        val sendMessage = new BukkitMessage(webhookManager, player, message);
        discord.addTask(config.getSendMessage("messages.player-chat", sendMessage));
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

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        webhookManager.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if (config.isSlowModePlayerChat()) {
            queueSlowly(e.getPlayer(), e.getMessage());
        } else {
            queueNow(e.getPlayer(), e.getMessage());
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        discord.addTask(config.getSendMessage(
                "messages.player-kick",
                Parameter.ofPlayer(player)
                        .put("%reason%", e.getReason())
        ));
    }
}
