package kr.entree.spicord.bukkit;

import kr.entree.spicord.config.PluginConfiguration;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class VerifiedMemberManager {
    private final Plugin plugin;
    private final Map<Long, UUID> mcByDiscord = new HashMap<>();
    private final Map<UUID, Long> discordByMc = new HashMap<>();

    public VerifiedMemberManager(Plugin plugin) {
        this.plugin = plugin;
    }

    private File createFile(Plugin plugin) {
        return new File(plugin.getDataFolder(), "verified.yml");
    }

    private void put(String discordId, String mcId) {
        long discordIdLong;
        UUID mcUuid;
        try {
            discordIdLong = Long.parseLong(discordId);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.INFO, "Not number: " + discordId);
            return;
        }
        try {
            mcUuid = UUID.fromString(mcId);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.INFO, "Not uuid: " + mcId);
            return;
        }
        put(discordIdLong, mcUuid);
    }

    public void load(Plugin plugin) {
        try {
            File file = createFile(plugin);
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            for (String discordId : config.getKeys(false)) {
                Object val = config.get(discordId);
                if (val != null) {
                    String mcId = val.toString();
                    put(discordId, mcId);
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            // Ignore
        }
    }

    public static String saveToString(Collection<Map.Entry<Long, UUID>> entries) {
        val config = new YamlConfiguration();
        for (val entry : entries) {
            config.set(entry.getKey().toString(), entry.getValue().toString());
        }
        return config.saveToString();
    }

    public void save(Plugin plugin, String contents) {
        try {
            PluginConfiguration.writeText(createFile(plugin), contents);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, e, () -> "Failed while saving verified members");
        }
    }

    public void saveAsync() {
        HashSet<Map.Entry<Long, UUID>> entries = new HashSet<>(mcByDiscord.entrySet());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> save(plugin, saveToString(entries)));
    }

    public void save(Plugin plugin) {
        save(plugin, saveToString(mcByDiscord.entrySet()));
    }

    public void put(Long discordUser, UUID mcUser) {
        mcByDiscord.put(discordUser, mcUser);
        discordByMc.put(mcUser, discordUser);
    }

    public UUID getMinecraft(Long discordUser) {
        return mcByDiscord.get(discordUser);
    }

    public Long getDiscord(UUID mcUser) {
        return discordByMc.get(mcUser);
    }
}
