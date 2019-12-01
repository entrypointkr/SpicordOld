package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.config.PluginConfiguration;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class VerifiedMemberManager {
    private final Plugin plugin;
    private final Map<Long, PlayerData> mcByDiscord = new ConcurrentHashMap<>();
    private final Map<UUID, Long> discordByMc = new ConcurrentHashMap<>();

    public VerifiedMemberManager(Plugin plugin) {
        this.plugin = plugin;
    }

    private File createFile(Plugin plugin) {
        return new File(plugin.getDataFolder(), "verified.yml");
    }

    public void update(@NotNull Player player) {
        PlayerData data = getMinecraft(player.getUniqueId());
        if (data != null) {
            data.name(player.getName());
        }
    }

    private void put(String discordId, String idAndName) {
        val pieces = idAndName.split("\\|", 2);
        long discordIdLong;
        UUID mcUuid;
        try {
            discordIdLong = Long.parseLong(discordId);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.INFO, "Not number: " + discordId);
            return;
        }
        try {
            mcUuid = UUID.fromString(pieces[0]);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.INFO, "Not uuid: " + pieces[0]);
            return;
        }
        val data = new PlayerData(mcUuid);
        if (pieces.length >= 2) {
            data.name(pieces[1]);
        }
        put(discordIdLong, data);
    }

    public void load(Plugin plugin) {
        File file = createFile(plugin);
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            for (String discordId : config.getKeys(false)) {
                Object val = config.get(discordId);
                if (val != null) {
                    String idAndName = val.toString();
                    put(discordId, idAndName);
                }
            }
        } catch (IOException e) {
            // Ignore
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, e, () -> "Failed while loading: " + file);
        }
    }

    public static String saveToString(Collection<Map.Entry<Long, PlayerData>> entries) {
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
        HashSet<Map.Entry<Long, PlayerData>> entries = new HashSet<>(mcByDiscord.entrySet());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> save(plugin, saveToString(entries)));
    }

    public void save(Plugin plugin) {
        save(plugin, saveToString(mcByDiscord.entrySet()));
    }

    public void put(Long discordUser, PlayerData mcUser) {
        mcByDiscord.put(discordUser, mcUser);
        discordByMc.put(mcUser.getId(), discordUser);
    }

    @Nullable
    public PlayerData getMinecraft(Long discordUser) {
        return mcByDiscord.get(discordUser);
    }

    @Nullable
    public PlayerData getMinecraft(UUID uuid) {
        Long discordId = getDiscord(uuid);
        if (discordId != null) {
            return getMinecraft(discordId);
        }
        return null;
    }

    @Nullable
    public Long getDiscord(UUID mcUser) {
        return discordByMc.get(mcUser);
    }
}
