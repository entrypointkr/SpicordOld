package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.Verification;
import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.GuildJoinEvent;
import kr.entree.spicord.bukkit.event.GuildLeaveEvent;
import kr.entree.spicord.bukkit.event.PrivateChatEvent;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.util.CooldownMap;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.config.*;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.channel.ChannelHandlerBuilder;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import kr.entree.spicord.discord.task.channel.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.guild.handler.AddRole;
import kr.entree.spicord.discord.task.guild.handler.CombinedMemberHandler;
import kr.entree.spicord.discord.task.guild.handler.GuildMemberHandler;
import kr.entree.spicord.discord.task.guild.handler.Rename;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PlayerVerifier implements Listener {
    private final Plugin plugin;
    private final SpicordConfig config;
    private final LangConfig langConfig;
    private final VerifiedMemberManager manager;
    private final Map<UUID, Verification> verifies = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final CooldownMap<Long> cooldowns = new CooldownMap<>();

    public PlayerVerifier(Plugin plugin, SpicordConfig config, LangConfig langConfig, VerifiedMemberManager manager) {
        this.plugin = plugin;
        this.config = config;
        this.langConfig = langConfig;
        this.manager = manager;
    }

    private VerificationConfig getConfig() {
        return config.getVerification();
    }

    public String generateCode(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                int start = 'a';
                int end = 'z';
                int rand = random.nextInt(end - start + 1);
                builder.append((char) (start + rand));
            } else {
                builder.append(random.nextInt(10));
            }
        }
        return builder.toString();
    }

    private BukkitTask createTask(UUID id, Discord discord, ChannelHandlerBuilder builder) {
        long seconds = getConfig().getExpireSeconds();
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            verifies.remove(id);
            Parameter parameter = new Parameter().put("%seconds%", seconds);
            builder.message(config, "verify-expired", parameter)
                    .queue(discord);
        }, seconds * 20L);
    }

    private void putVerification(UUID id, Verification verification) {
        Verification prev = verifies.put(id, verification);
        if (prev != null) {
            prev.cancelExpireTask();
        }
    }

    private boolean checkCooldown(Long userId, Parameter parameter) {
        val cools = getConfig().getCooldownSeconds() * 1000L;
        long remain = cooldowns.action(userId, cools);
        if (remain > 0) {
            parameter.put("%seconds%", remain / 1000);
            return false;
        }
        return true;
    }

    private void processCommand(Message message, Discord discord) {
        val verifyConfig = getConfig();
        if (!verifyConfig.isEnabled()) {
            return;
        }
        val prefix = verifyConfig.getVerificationCommandPrefix();
        val contents = message.getContents();
        if (!contents.startsWith(prefix)) {
            return;
        }
        val author = message.getAuthor();
        val minecraft = manager.getMinecraft(author.getId());
        val builder = new ChannelHandlerBuilder()
                .channel(PrivateChannelOpener.of(author.getId()));
        val parameter = new Parameter().put(author);
        if (minecraft != null) {
            builder.message(config, "already-verified", parameter.put("%uuid%", minecraft))
                    .queue(discord);
            return;
        }
        parameter.put("%command%", prefix);
        val pieces = contents.split(" ", 2);
        if (pieces.length >= 2) {
            val argument = pieces[1];
            val player = Bukkit.getPlayer(argument);
            parameter.put("%name%", argument);
            if (player != null) {
                if (!checkCooldown(author.getId(), parameter)) {
                    builder.message(config, "verify-cooldown", parameter)
                            .queue(discord);
                    return;
                }
                parameter.put(player);
                val discordId = manager.getDiscord(player.getUniqueId());
                if (discordId != null) {
                    builder.message(config, "already-verified", parameter)
                            .queue(discord);
                    return;
                }
                val code = generateCode(6);
                val id = player.getUniqueId();
                parameter.put("%code%", code);
                putVerification(id, new Verification(player, author, code, createTask(id, discord, builder), discord));
                builder.message(config, "verify-code", parameter)
                        .queue(discord);
                player.sendMessage(langConfig.format(Lang.VERIFY_MESSAGE, parameter));
            } else {
                builder.message(config, "player-offline", parameter)
                        .queue(discord);
            }
        } else {
            builder.message(config, "verify-usage", parameter)
                    .queue(discord);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        val player = e.getPlayer();
        val verification = verifies.remove(player.getUniqueId());
        if (verification == null) {
            return;
        }
        val user = verification.getUser();
        val parameter = new Parameter().put(user)
                .put(player);
        if (verification.match(e.getMessage().toLowerCase())) {
            val verifyConfig = getConfig();
            manager.put(user.getId(), new PlayerData(verification.getUuid()).name(player.getName()));
            manager.saveAsync();
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendMessage(langConfig.format(Lang.VERIFY_SUCCESS, parameter));
                verifyConfig.executeCommands(Bukkit.getConsoleSender(), parameter);
            });
            Arrays.asList(
                    new ChannelTask(
                            PrivateChannelOpener.of(user.getId()),
                            this.config.getMessage("verify-success", parameter)
                    ),
                    GuildMemberHandler.createTask(
                            this.config.getGuild().getLong(),
                            verification.getUser().getId(),
                            new AddRole(verifyConfig::getDiscordRoles),
                            new Rename(verifyConfig.getDiscordName(player.getName()))
                    )
            ).forEach(verification.getDiscord()::addTask);
            e.setCancelled(true);
        } else {
            verification.getDiscord().addTask(new ChannelTask(
                    PrivateChannelOpener.of(user.getId()),
                    config.getMessage("verify-failed", parameter)
            ));
        }
        verification.cancelExpireTask();
    }

    @EventHandler
    public void onGuildChat(GuildChatEvent e) {
        val author = e.getAuthor();
        if (author.isBot()) {
            return;
        }
        String channel = e.getChannel().toString();
        String verifyChannel = config.remapChannel(getConfig().getChannel());
        if (!verifyChannel.equals(channel)) {
            return;
        }
        processCommand(e.getMessage(), e.getDiscord());
    }

    @EventHandler
    public void onGuildLeave(GuildLeaveEvent e) {
        if (config.getVerification().isDeleteIfQuitFromGuild()) {
            manager.remove(e.getUser().getId());
        }
    }

    @EventHandler
    public void onGuildJoin(GuildJoinEvent e) {
        val user = e.getUser();
        val mcUser = manager.getMinecraft(user.getId());
        if (mcUser != null) {
            val name = mcUser.getName();
            val handlers = new CombinedMemberHandler()
                    .add(new AddRole(getConfig()::getDiscordRoles));
            if (name != null) {
                handlers.add(new Rename(getConfig().getDiscordName(name)));
            }
            e.getDiscord().addTask(
                    GuildMemberHandler.createTask(
                            this.config.getGuild().getLong(),
                            user.getId(),
                            handlers
                    )
            );
        }
    }

    @EventHandler
    public void onPrivateChat(PrivateChatEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        processCommand(e.getMessage(), e.getDiscord());
    }
}
