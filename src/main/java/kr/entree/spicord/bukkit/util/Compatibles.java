package kr.entree.spicord.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-25
 */
public class Compatibles {
    private Compatibles() {
    }

    @SuppressWarnings("unchecked")
    public static Collection<Player> getOnlinePlayers() {
        Collection<Player> ret;
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            ret = onlinePlayersMethod.getReturnType().equals(Collection.class)
                    ? ((Collection<Player>) onlinePlayersMethod.invoke(Bukkit.getServer()))
                    : Arrays.asList((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer()));
        } catch (Exception e) {
            ret = (Collection<Player>) Bukkit.getOnlinePlayers();
        }
        return ret;
    }

    public static Optional<Player> getPlayer(UUID id) {
        try {
            return Optional.ofNullable(Bukkit.getPlayer(id));
        } catch (Exception ex) {
            return getOnlinePlayers().stream()
                    .filter(p -> p.getUniqueId().equals(id))
                    .findAny();
        }
    }

    public static Optional<OfflinePlayer> getOfflinePlayer(UUID id) {
        OfflinePlayer ret = null;
        try {
            ret = Bukkit.getOfflinePlayer(id);
        } catch (Exception ex) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (player.getUniqueId().equals(id)) {
                    ret = player;
                    break;
                }
            }
        }
        return Optional.ofNullable(ret);
    }
}
