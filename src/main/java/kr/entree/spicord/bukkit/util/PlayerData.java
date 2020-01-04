package kr.entree.spicord.bukkit.util;

import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class PlayerData {
    @Getter
    private final UUID id;
    @Nullable
    private String name;

    public PlayerData(UUID id) {
        this.id = id;
    }

    public PlayerData name(@Nullable String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(id.toString());
        if (name != null) {
            builder.append('|').append(name);
        }
        return builder.toString();
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(id);
    }

    @Nullable
    public String getName() {
        val player = getPlayer();
        if (player != null) {
            return player.getName();
        }
        return name;
    }
}
