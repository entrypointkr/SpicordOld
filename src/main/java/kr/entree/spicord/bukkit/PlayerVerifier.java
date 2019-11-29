package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.event.PrivateChatEvent;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.config.VerificationConfig;
import kr.entree.spicord.discord.ChannelSupplier;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.supplier.TextChannelSupplier;
import kr.entree.spicord.discord.task.ChannelHandler;
import lombok.val;
import net.dv8tion.jda.api.entities.MessageChannel;
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

    private BukkitTask createTask(UUID id, Discord discord, ChannelSupplier<? extends MessageChannel> supplier) {
        long seconds = getConfig().getExpireSeconds();
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            verifies.remove(id);
            discord.addTask(ChannelHandler.of(
                    supplier,
                    config.getMessage(
                            "verify-expired",
                            new Parameter().put("%seconds%", seconds)
                    )
            ));
        }, seconds);
    }

    private void putVerification(UUID id, Verification verification) {
        Verification prev = verifies.put(id, verification);
        if (prev != null) {
            prev.cancelExpireTask();
        }
    }

    private void processCommand(Message message, Discord discord, ChannelSupplier<? extends MessageChannel> supplier) {
        if (!getConfig().isEnabled()) {
            return;
        }
        val contents = message.getContents();
        val author = message.getAuthor();
        val minecraft = manager.getMinecraft(author.getId());
        if (minecraft != null) {
            discord.addTask(ChannelHandler.of(supplier, config.getMessage(
                    "already-verified",
                    new Parameter().put("%uuid%", minecraft)
            )));
            return;
        }
        val prefix = getConfig().getVerificationCommandPrefix();
        if (contents.startsWith(prefix) && contents.length() > prefix.length()) {
            val argument = contents.substring(prefix.length() + 1);
            val player = Bukkit.getPlayer(argument);
            if (player != null) {
                val code = generateCode(6);
                val id = player.getUniqueId();
                putVerification(id, new Verification(player, code, createTask(id, discord, supplier), author.getId(), discord));
                discord.addTask(ChannelHandler.of(supplier, config.getMessage(
                        "verify-code",
                        new Parameter().put(player)
                                .put("%code%", code)
                )));
                player.sendMessage(langConfig.format(
                        Lang.VERIFY_MESSAGE,
                        new Parameter().put("%discord%", author.getName())
                ));
            } else {
                discord.addTask(ChannelHandler.of(
                        supplier,
                        config.getMessage(
                                "player-offline",
                                new Parameter().put("%name%", argument)
                        )
                ));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Verification verification = verifies.remove(e.getPlayer().getUniqueId());
        if (verification == null) {
            return;
        }
        if (verification.match(e.getMessage().toLowerCase())) {
            manager.put(verification.getDiscordId(), verification.getUuid());
            Parameter parameter = new Parameter().put("%discord%", verification.getUuid())
                    .put(e.getPlayer());
            e.getPlayer().sendMessage(langConfig.format(Lang.VERIFY_SUCCESS, parameter));
            verification.getDiscord().addTask(ChannelHandler.of(
                    new PrivateChannelOpener(verification.getDiscordId()),
                    config.getMessage("verify-success", parameter)
            ));
            verification.cancelExpireTask();
        } else {
            Parameter parameter = new Parameter();
            e.getPlayer().sendMessage(langConfig.format(Lang.VERIFY_FAILED, parameter));
            verification.getDiscord().addTask(ChannelHandler.of(
                    new PrivateChannelOpener(verification.getDiscordId()),
                    config.getMessage("verify-failed", parameter)
            ));
        }
    }

    @EventHandler
    public void onGuildChat(GuildChatEvent e) {
        val author = e.getAuthor();
        if (author.isBot()) {
            return;
        }
        String channel = e.getChannel().toString();
        String verifyChannel = config.formatChannel(getConfig().getChannel());
        if (!verifyChannel.equals(channel)) {
            return;
        }
        processCommand(e.getMessage(), e.getDiscord(), TextChannelSupplier.of(e.getChannel().toString()));
    }

    @EventHandler
    public void onPrivateChat(PrivateChatEvent e) {
        processCommand(e.getMessage(), e.getDiscord(), new PrivateChannelOpener(e.getAuthor().getId()));
    }
}
