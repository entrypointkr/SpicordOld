package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.messenger.Messenger;
import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import lombok.val;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import static kr.entree.spicord.config.SpicordConfig.featureKey;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class ChatToDiscord implements Listener {
    private final Plugin plugin;
    private final SpicordConfig config;
    private final Messenger textMessenger;
    private final Messenger webhookMessenger;

    @Inject
    public ChatToDiscord(
            Plugin plugin,
            SpicordConfig config,
            @Named("textMessenger") Messenger textMessenger,
            @Named("webhookMessenger") Messenger webhookMessenger
    ) {
        this.plugin = plugin;
        this.config = config;
        this.textMessenger = textMessenger;
        this.webhookMessenger = webhookMessenger;
    }

    private void chats(@Nullable Player player, String message) {
        chats(Chat.create(player, message));
    }

    private void chats(Chat chat) {
        if (Bukkit.isPrimaryThread()) {
            if (config.getFakeProfilePlayerChat().isTrue()) {
                webhookMessenger.chat(chat);
            } else {
                textMessenger.chat(chat);
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Validate.isTrue(Bukkit.isPrimaryThread());
                chats(chat);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled() && isIgnoreCancelled()) {
            return;
        }
        chats(e.getPlayer(), e.getMessage());
    }

    private boolean isJoinQuitEnabled() {
        return config.getBoolean(featureKey("player-chat.join-quit"), true);
    }

    private boolean isIgnoreCancelled() {
        return config.getBoolean(featureKey("player-chat.ignore-cancel"), true);
    }

    @Nullable
    private String getJoinMessage() {
        return config.getString(featureKey("player-chat.join-message"));
    }

    @Nullable
    private String getQuitMessage() {
        return config.getString(featureKey("player-chat.quit-message"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (!isJoinQuitEnabled()) return;
        val player = e.getPlayer();
        val altMsg = getQuitMessage();
        val message = altMsg != null
                ? new Parameter().put(player).format(altMsg)
                : e.getQuitMessage();
        chats(Chat.create(player, message).prefix(false));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!isJoinQuitEnabled()) return;
        val player = e.getPlayer();
        val altMsg = getJoinMessage();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            val message = altMsg != null
                    ? new Parameter().put(player).format(altMsg)
                    : e.getJoinMessage();
            chats(Chat.create(player, message).prefix(false));
        }, 2);
    }
}
