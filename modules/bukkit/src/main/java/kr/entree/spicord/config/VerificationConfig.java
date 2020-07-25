package kr.entree.spicord.config;

import io.vavr.control.Option;
import kr.entree.spicord.bukkit.restrict.RestrictType;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.util.Parameter;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class VerificationConfig {
    private final ConfigurationSection section;
    private final Logger logger;
    private Set<RestrictType> restricts = null;

    public VerificationConfig(ConfigurationSection section, Logger logger) {
        this.section = section;
        this.logger = logger;
    }

    public boolean isEnabled() {
        return section.getBoolean("enabled");
    }

    public String getChannel() {
        return section.getString("channel");
    }

    public String getVerificationCommandPrefix() {
        return section.getString("command");
    }

    public Set<RestrictType> getPlayerRestricts() {
        if (restricts == null) {
            restricts = new HashSet<>();
            for (String typeStr : section.getStringList("before.player-restrict")) {
                try {
                    RestrictType type = RestrictType.valueOf(typeStr.toUpperCase());
                    restricts.add(type);
                } catch (IllegalArgumentException ex) {
                    logger.log(Level.WARNING, "Unknown restrict type: {0}", typeStr);
                }
            }
        }
        return restricts;
    }

    public List<String> getDiscordRoles() {
        return section.getStringList("after.discord-role");
    }

    public List<Role> getDiscordRoles(Guild guild) {
        List<Role> ret = new ArrayList<>();
        for (String roleStr : getDiscordRoles()) {
            Role role = null;
            try {
                role = guild.getRoleById(roleStr);
            } catch (Exception ex) {
                // Ignore
            }
            if (role != null) {
                ret.add(role);
            } else {
                List<Role> roles = guild.getRolesByName(roleStr, true);
                if (roles.isEmpty()) {
                    logger.log(Level.INFO, "Unknown role name: {0}", roleStr);
                } else {
                    ret.addAll(roles);
                }
            }
        }
        return ret;
    }

    public List<String> getCommands() {
        return section.getStringList("after.command");
    }

    public void executeCommands(CommandSender sender, Parameter parameter) {
        for (String command : getCommands()) {
            Bukkit.dispatchCommand(sender, parameter.format(command));
        }
    }

    public long getExpireSeconds() {
        return section.getLong("expire-seconds");
    }

    public long getCooldownSeconds() {
        return section.getLong("cooldown-seconds");
    }

    public boolean isNameSync() {
        return section.getBoolean("after.name-sync");
    }

    public boolean isNameColorize() {
        return section.getBoolean("after.name-colorize");
    }

    public boolean isNameColorizeOpOnly() {
        return section.getBoolean("after.name-colorize");
    }

    public String getDiscordName(@NotNull String name) {
        return getDiscordNameFormat().replace("%name%", name);
    }

    public String getDiscordName(PlayerData data) {
        val parameter = Parameters.putPlayerData(new Parameter(), data);
        // Strip colors from name keys
        Arrays.asList("%name%", "%display-name%").forEach(key ->
                Option.of(parameter.get(key)).peek(v ->
                        parameter.put(key, ChatColor.stripColor(v.toString()))));
        return parameter.format(getDiscordNameFormat());
    }

    public String getDiscordNameFormat() {
        return section.getString("after.discord-name-format", "%name%");
    }

    public boolean isDeleteIfQuitFromGuild() {
        return section.getBoolean("delete-if-quit");
    }
}