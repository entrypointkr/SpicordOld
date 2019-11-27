package kr.entree.spicord.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class Verification {
    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final String code;

    public Verification(UUID uuid, String name, String code) {
        this.uuid = uuid;
        this.name = name;
        this.code = code;
    }

    public Verification(Player player, String code) {
        this(player.getUniqueId(), player.getName(), code);
    }

    public boolean match(String code) {
        return this.code.equalsIgnoreCase(code);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
}
