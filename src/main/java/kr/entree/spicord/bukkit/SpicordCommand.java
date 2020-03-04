package kr.entree.spicord.bukkit;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.CompleterBuilder;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import kr.entree.spicord.discord.task.channel.handler.EmptyMessageChannelHandler;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import kr.entree.spicord.discord.task.channel.handler.PlainMessage;
import kr.entree.spicord.discord.task.channel.supplier.TextChannelSupplier;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class SpicordCommand implements CommandExecutor, TabExecutor {
    private final Spicord plugin;
    private final Discord discord;
    private final Map<String, Set<String>> recentInputChannelIds = new HashMap<>();

    public SpicordCommand(Spicord plugin, Discord discord) {
        this.plugin = plugin;
        this.discord = discord;
    }

    private void done(CommandSender sender) {
        sender.sendMessage("Done!");
    }

    private void noPermission(CommandSender sender) {
        sender.sendMessage(plugin.getLangConfig().format(Lang.NO_PERMISSION));
    }

    private void addRecentInputChannelId(CommandSender sender, String id) {
        recentInputChannelIds.computeIfAbsent(sender.getName(), k -> new HashSet<>()).add(id);
    }

    private Set<String> getRecentInputChannelIds(String name) {
        return recentInputChannelIds.getOrDefault(name, Collections.emptySet());
    }

    private boolean checkPermission(CommandSender sender) {
        return sender.hasPermission("spicord.command");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return true;
        }
        SpicordConfig config = plugin.spicordConfig();
        if (args.length >= 1) {
            String head = args[0].toLowerCase();
            switch (head) {
                case "reload":
                    plugin.loadConfigs();
                    done(sender);
                    return true;
                case "send":
                    if (args.length >= 3) {
                        val channel = args[1];
                        val message = args[2];
                        MessageChannelHandler messageHandler;
                        if (config.contains("messages." + message)) {
                            val parameter = new Parameter();
                            if (args.length >= 4) {
                                parameter.putSerialized(args[3]);
                            }
                            messageHandler = config.getMessage(message, parameter);
                        } else {
                            messageHandler = new PlainMessage(StringUtils.join(args, ' ', 2, args.length));
                        }
                        if (messageHandler instanceof EmptyMessageChannelHandler) {
                            messageHandler = new PlainMessage(message);
                        }
                        val handler = new ChannelTask(
                                TextChannelSupplier.ofConfigurized(config, channel),
                                messageHandler
                        );
                        discord.addTask(new CompleterBuilder(handler)
                                .success(() -> addRecentInputChannelId(sender, channel))
                                .failure(plugin.getLogger())
                                .build());
                        done(sender);
                    } else {
                        sender.sendMessage(String.format("/%s send (channel) (message)", label));
                    }
                    return true;
                case "saveverify":
                    plugin.getVerifyManager().save(plugin);
                    done(sender);
                    return true;
                case "reloadverify":
                    plugin.getVerifyManager().load(plugin);
                    done(sender);
                    return true;
            }
        }
        sender.sendMessage(String.format("/%s (reload|send|saveverify|reloadverify)", label));
        return true;
    }

    private List<String> matching(Collection<String> completes, String input) {
        return completes.stream()
                .filter(complete -> complete.startsWith(input))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!checkPermission(sender)) {
            return null;
        }
        if (args.length == 1) {
            return matching(
                    Arrays.asList("reload", "send", "saveverify", "reloadverify"),
                    args[0]
            );
        } else if ("send".equalsIgnoreCase(args[0])) {
            ConfigurationSection section = null;
            String argument = null;
            if (args.length == 2) {
                section = plugin.spicordConfig().getChannelSection();
                argument = args[1];
            } else if (args.length == 3) {
                section = plugin.spicordConfig().getMessageSection();
                argument = args[2];
            }
            if (section != null) {
                val ret = new LinkedHashSet<>(section.getKeys(false));
                ret.addAll(getRecentInputChannelIds(sender.getName()));
                return matching(ret, argument);
            }
        }
        return null;
    }
}
