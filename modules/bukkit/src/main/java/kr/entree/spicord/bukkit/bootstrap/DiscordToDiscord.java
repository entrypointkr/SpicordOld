package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.GuildJoinEvent;
import kr.entree.spicord.bukkit.event.GuildMemberEvent;
import kr.entree.spicord.bukkit.event.GuildMemberNamingEvent;
import kr.entree.spicord.bukkit.util.Platform;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import kr.entree.spicord.discord.task.channel.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.guild.handler.GuildMemberHandler;
import kr.entree.spicord.discord.task.guild.handler.Rename;
import kr.entree.spicord.util.Parameter;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class DiscordToDiscord implements Listener {
    private final SpicordConfig config;
    private final VerifiedMemberManager verifyManager;

    @Inject
    public DiscordToDiscord(SpicordConfig config, VerifiedMemberManager verifyManager) {
        this.config = config;
        this.verifyManager = verifyManager;
    }

    @EventHandler
    public void onGuildJoin(GuildJoinEvent e) {
        if (!config.isEnabled(SpicordConfig.featureKey("welcome"))) {
            return;
        }
        if (e.getUser().isBot()) {
            return;
        }
        val parameter = new Parameter().put(e.getUser());
        e.getDiscord().addTask(new ChannelTask(
                PrivateChannelOpener.of(e.getUser().getId()),
                config.getMessage("welcome", parameter)
        ));
    }

    private void syncName(Discord discord, long guildId, long userId, @Nullable String newName) {
        val verifyConfig = config.getVerification();
        if (!verifyConfig.isNameSync()) {
            return;
        }
        val mcId = verifyManager.getMinecraft(userId);
        if (mcId == null) {
            return;
        }
        val offlinePlayer = Platform.getOfflinePlayer(mcId.getId());
        val name = offlinePlayer.getName();
        if (name == null || name.equals(newName)) return;
        discord.addTask(GuildMemberHandler.createTask(guildId, userId, new Rename(verifyConfig.getDiscordName(mcId))));
        config.tryColorizeDiscordName(mcId, userId, guildId);
    }

    private void syncName(GuildMemberEvent e, @Nullable String newName) {
        syncName(e.getDiscord(), e.getGuild().getId(), e.getMember().getId(), newName);
    }

    @EventHandler
    public void onNaming(GuildMemberNamingEvent e) {
        syncName(e, e.getTo());
    }

    @EventHandler
    public void onChat(GuildChatEvent e) {
        syncName(e, null);
    }
}
