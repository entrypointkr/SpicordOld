package kr.entree.spicord.bukkit.util;

/**
 * Created by JunHyung Lim on 2019-11-27
 */
public class Cooldown {
    private long last = 0;

    public long action(long coolMillis) {
        if (coolMillis <= 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        long diff = now - last;
        if (diff >= coolMillis) {
            last = now;
            return 0;
        }
        return coolMillis - diff;
    }

    public long getLast() {
        return last;
    }
}
