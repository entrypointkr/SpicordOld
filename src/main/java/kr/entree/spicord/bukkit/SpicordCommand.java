package kr.entree.spicord.bukkit;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.ChannelHandler;
import kr.entree.spicord.discord.task.CompleterBuilder;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private void addRecentInputChannelId(CommandSender sender, String id) {
        recentInputChannelIds.computeIfAbsent(sender.getName(), k -> new HashSet<>()).add(id);
    }

    private Set<String> getRecentInputChannelIds(String name) {
        return recentInputChannelIds.getOrDefault(name, Collections.emptySet());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            String head = args[0].toLowerCase();
            if ("reload".equals(head)) {
                plugin.loadConfigs();
                done(sender);
                return true;
            } else if ("send".equals(head)) {
                if (args.length >= 3) {
                    val channel = args[1];
                    val message = StringUtils.join(args, ' ', 2, args.length);
                    val handler = ChannelHandler.ofText(plugin.getSpicordConfig(), channel, message);
                    discord.addTask(new CompleterBuilder(handler)
                            .success(() -> addRecentInputChannelId(sender, channel))
                            .failure(plugin.getLogger())
                            .build());
                    done(sender);
                } else {
                    sender.sendMessage(String.format("/%s send (channel) (message)", label));
                }
                return true;
            }
        }
        sender.sendMessage(String.format("/%s (reload|send)", label));
        return true;
    }

    private List<String> matching(Collection<String> completes, String input) {
        return completes.stream()
                .filter(complete -> complete.startsWith(input))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return matching(Arrays.asList("reload", "send"), args[0]);
        } else if (args.length >= 2 && "send".equalsIgnoreCase(args[0])) {
            val section = plugin.getSpicordConfig().getChannelSection();
            if (section != null) {
                val ret = new LinkedHashSet<>(section.getKeys(false));
                ret.addAll(getRecentInputChannelIds(sender.getName()));
                return matching(ret, args[1]);
            }
        }
        return null;
    }
}
