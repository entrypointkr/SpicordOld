package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.event.GuildBoostEvent;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.GuildJoinEvent;
import kr.entree.spicord.bukkit.event.GuildLeaveEvent;
import kr.entree.spicord.bukkit.event.PrivateChatEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class DiscordEventToBukkit extends ListenerAdapter {
    private final Plugin plugin;

    public DiscordEventToBukkit(Plugin plugin) {
        this.plugin = plugin;
    }

    private void callEvent(Event event) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.isWebhookMessage()) {
            callEvent(GuildChatEvent.from(event));
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        callEvent(GuildJoinEvent.from(event));
    }

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        callEvent(GuildLeaveEvent.from(event));
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@Nonnull GuildMemberUpdateBoostTimeEvent event) {
        callEvent(GuildBoostEvent.from(event));
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        callEvent(PrivateChatEvent.from(event));
    }
}
