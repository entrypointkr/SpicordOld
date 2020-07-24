package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.config.SpicordConfig;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class DiscordToBukkit implements Listener {
    private final Plugin plugin;
    private final SpicordConfig config;

    @Inject
    public DiscordToBukkit(Plugin plugin, SpicordConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onGuildChat(GuildChatEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        String baseKey = SpicordConfig.featureKey("discord-chat");
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
