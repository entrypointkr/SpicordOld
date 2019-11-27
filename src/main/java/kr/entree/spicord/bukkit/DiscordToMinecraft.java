package kr.entree.spicord.bukkit;

import kr.entree.spicord.config.SpicordConfig;
import lombok.val;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class DiscordToMinecraft extends ListenerAdapter {
    public static final String KEY = "messages.discord-chat";
    public static final String CHANNELS_KEY = KEY + ".channel";
    private final Plugin plugin;
    private final SpicordConfig config;

    public DiscordToMinecraft(Plugin plugin, SpicordConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }
        if (!config.isEnabled(KEY)) {
            return;
        }
        val channels = config.getChannelIds(CHANNELS_KEY);
        if (!channels.contains(event.getChannel().getId())) {
            return;
        }
        val discordChat = config.getDiscordChat();
        Bukkit.getScheduler().runTask(plugin, () ->
                discordChat.send(event.getMessage()));
    }
}
