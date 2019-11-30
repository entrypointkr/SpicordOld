package kr.entree.spicord.bukkit.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JunHyung Lim on 2019-11-27
 */
public class CooldownMap<K> {
    private final Map<K, Cooldown> map = new HashMap<>();

    public Cooldown get(K id) {
        return map.computeIfAbsent(id, k -> new Cooldown());
    }

    public Cooldown remove(K id) {
        return map.remove(id);
    }

    public long action(K id, long coolMillis) {
        return get(id).action(coolMillis);
    }
}
