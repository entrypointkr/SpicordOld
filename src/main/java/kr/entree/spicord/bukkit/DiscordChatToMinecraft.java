package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.config.SpicordConfig;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class DiscordChatToMinecraft implements Listener {
    public static final String KEY = "events.discord-chat";
    public static final String CHANNELS_KEY = KEY + ".channel";
    private final Plugin plugin;
    private final SpicordConfig config;

    public DiscordChatToMinecraft(Plugin plugin, SpicordConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onGuildChat(GuildChatEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        if (!config.isEnabled(KEY)) {
            return;
        }
        val channels = config.getChannelIds(CHANNELS_KEY);
        if (!channels.contains(e.getChannel().toString())) {
            return;
        }
        val discordChat = config.getDiscordChat();
        Bukkit.getScheduler().runTask(plugin, () ->
                discordChat.send(e.getMember().getName(), e.getMessage().getContents()));
    }
}
