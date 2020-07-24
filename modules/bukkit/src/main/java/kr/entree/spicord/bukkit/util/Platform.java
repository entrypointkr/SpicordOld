package kr.entree.spicord.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-25
 */
public class Platform {
    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    @Nullable
    public static Player getPlayer(UUID id) {
        return Bukkit.getPlayer(id);
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        return Bukkit.getOfflinePlayer(id);
    }
}
