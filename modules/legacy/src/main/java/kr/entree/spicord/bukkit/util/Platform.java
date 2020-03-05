package kr.entree.spicord.bukkit.util;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by JunHyung Lim on 2020-03-05
 */
public class Platform {
    public static Collection<? extends Player> getOnlinePlayers() {
        return new ArrayList<>(Arrays.asList(Bukkit.getOnlinePlayers()));
    }

    @Nullable
    public static Player getPlayer(UUID id) {
        return Arrays.stream(Bukkit.getOnlinePlayers())
                .filter(player -> player.getUniqueId().equals(id))
                .findAny()
                .orElse(null);
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        val onlinePlayer = getPlayer(id);
        return onlinePlayer != null ? onlinePlayer : new EmptyOfflinePlayer(id);
    }

    public static class EmptyOfflinePlayer implements OfflinePlayer {
        private final UUID uuid;

        public EmptyOfflinePlayer(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public boolean isOnline() {
            return false;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isBanned() {
            return false;
        }

        @Override
        public void setBanned(boolean b) {

        }

        @Override
        public boolean isWhitelisted() {
            return false;
        }

        @Override
        public void setWhitelisted(boolean b) {

        }

        @Override
        public Player getPlayer() {
            return Platform.getPlayer(uuid);
        }

        @Override
        public long getFirstPlayed() {
            return 0;
        }

        @Override
        public long getLastPlayed() {
            return 0;
        }

        @Override
        public boolean hasPlayedBefore() {
            return false;
        }

        @Override
        public Location getBedSpawnLocation() {
            return null;
        }

        @Override
        public Map<String, Object> serialize() {
            return Collections.emptyMap();
        }

        @Override
        public boolean isOp() {
            return false;
        }

        @Override
        public void setOp(boolean b) {

        }
    }
}
