package kr.entree.spicord.config;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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

    public void send(Message message) {
        if (!enabled) {
            return;
        }
        User author = message.getAuthor();
        String contents = message.getContentDisplay();
        String formatted = colorize(format.replace("%name%", author.getName())
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