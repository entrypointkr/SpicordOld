package kr.entree.spicord.option.config.getter;

import kr.entree.spicord.option.config.ConfigGetter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public abstract class AssociativeGetter<T> implements ConfigGetter<T> {
    protected final String key;

    public AssociativeGetter(String key) {
        this.key = key;
    }

    @Override
    public final T get(ConfigurationSection config) {
        return get(config, key);
    }

    public abstract T get(ConfigurationSection config, String key);
}
