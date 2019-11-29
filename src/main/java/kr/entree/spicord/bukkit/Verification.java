package kr.entree.spicord.bukkit;

import kr.entree.spicord.discord.Discord;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class Verification {
    @Getter
    private final UUID uuid;
    @Getter
    private final Long discordId;
    @Getter
    private final String name;
    @Getter
    private final String code;
    @Getter
    private final BukkitTask expireTask;
    @Getter
    private final Discord discord;

    public Verification(UUID uuid, Long discordId, String name, String code, BukkitTask expireTask, Discord discord) {
        this.uuid = uuid;
        this.discordId = discordId;
        this.name = name;
        this.code = code;
        this.expireTask = expireTask;
        this.discord = discord;
    }

    public Verification(Player player, String code, BukkitTask expireTask, Long discordId, Discord discord) {
        this(player.getUniqueId(), discordId, player.getName(), code, expireTask, discord);
    }

    public boolean match(String code) {
        return this.code.equalsIgnoreCase(code);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public void cancelExpireTask() {
        if (!expireTask.isCancelled()) {
            expireTask.cancel();
        }
    }
}
