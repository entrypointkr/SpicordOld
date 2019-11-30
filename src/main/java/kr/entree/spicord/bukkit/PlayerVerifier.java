package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.PrivateChatEvent;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.util.CooldownMap;
import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.config.VerificationConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.ChannelHandler;
import kr.entree.spicord.discord.task.ChannelHandlerBuilder;
import lombok.val;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class PlayerVerifier implements Listener {
    private final Plugin plugin;
    private final SpicordConfig config;
    private final LangConfig langConfig;
    private final VerifiedMemberManager manager;
    private final Map<UUID, Verification> verifies = new HashMap<>();
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

    private BukkitTask createTask(UUID id, Discord discord, ChannelHandlerBuilder<?> builder) {
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
        val builder = new ChannelHandlerBuilder<PrivateChannel>()
                .channel(new PrivateChannelOpener(author.getId()));
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
                val code = generateCode(6);
                val id = player.getUniqueId();
                parameter.put(player).put("%code%", code);
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
            manager.put(user.getId(), verification.getUuid());
            player.sendMessage(langConfig.format(Lang.VERIFY_SUCCESS, parameter));
            verification.getDiscord().addTask(ChannelHandler.of(
                    new PrivateChannelOpener(user.getId()),
                    config.getMessage("verify-success", parameter)
            ));
            getConfig().executeCommands(Bukkit.getConsoleSender(), parameter);
            verification.getDiscord().addTask(jda -> {
                val guild = config.getGuild(jda);
                if (guild == null) {
                    return;
                }
                val roles = getConfig().getDiscordRoles(guild);
                val member = guild.getMemberById(verification.getUser().getId());
                if (member == null) {
                    return;
                }
                for (Role role : roles) {
                    guild.addRoleToMember(member, role).queue();
                }
            });
            e.setCancelled(true);
        } else {
            verification.getDiscord().addTask(ChannelHandler.of(
                    new PrivateChannelOpener(user.getId()),
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
    public void onPrivateChat(PrivateChatEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        processCommand(e.getMessage(), e.getDiscord());
    }
}
