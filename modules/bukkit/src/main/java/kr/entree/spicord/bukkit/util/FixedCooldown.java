package kr.entree.spicord.bukkit.util;

import java.time.Duration;

/**
 * Created by JunHyung Lim on 2020-03-07
 */
public class FixedCooldown {
    private final Duration coolDuration;
    private final Cooldown cooldown;

    public FixedCooldown(Duration coolDuration, Cooldown cooldown) {
        this.coolDuration = coolDuration;
        this.cooldown = cooldown;
    }

    public long action() {
        return cooldown.action(coolDuration.toMillis());
    }

    public boolean actions() {
        return action() <= 0;
    }
}
