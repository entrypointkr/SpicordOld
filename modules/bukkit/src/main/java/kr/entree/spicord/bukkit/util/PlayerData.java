package kr.entree.spicord.bukkit.util;

import io.vavr.control.Option;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
@Data
public class PlayerData {
    @Getter
    private final UUID id;
    @Nullable
    private String name;
    private String displayName;

    public PlayerData(UUID id) {
        this.id = id;
    }

    public PlayerData(PlayerData data) {
        this(data.id);
        name(data.name);
        displayName(data.displayName);
    }

    public PlayerData(Player player) {
        this(player.getUniqueId());
        name(player.getName());
        displayName(player.getDisplayName());
    }

    public PlayerData name(@Nullable String name) {
        this.name = name;
        return this;
    }

    public PlayerData displayName(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayNameOrDefault() {
        val ret = displayName != null ? displayName : name;
        return ret != null ? ret : "unknown";
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
        return Platform.getPlayer(id);
    }

    public Option<Player> getPlayerOption() {
        return Option.of(getPlayer());
    }

    public boolean isOp() {
        return getPlayerOption().map(ServerOperator::isOp).getOrElse(false);
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
