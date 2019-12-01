package kr.entree.spicord.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class MinecraftChat {
    @Getter
    private final boolean enabled;
    @Getter
    private final String format;
    @Getter
    private final Collection<String> worlds;

    public MinecraftChat(boolean enabled, String format, Collection<String> worlds) {
        this.enabled = enabled;
        this.format = format;
        this.worlds = worlds;
    }

    public static String colorize(String contents) {
        return ChatColor.translateAlternateColorCodes('&', contents);
    }

    public void send(String author, String contents) {
        if (!enabled) {
            return;
        }
        String formatted = colorize(format.replace("%name%", author)
                .replace("%message%", contents));
        if (worlds.isEmpty()) {
            Bukkit.broadcastMessage(formatted);
        } else {
            for (String worldStr : worlds) {
                sendToWorld(worldStr, formatted);
            }
            Bukkit.getConsoleSender().sendMessage(formatted);
        }
    }

    private void sendToWorld(String worldStr, String message) {
        World world = Bukkit.getWorld(worldStr);
        if (world == null) {
            return;
        }
        for (Player player : world.getPlayers()) {
            player.sendMessage(message);
        }
    }
}