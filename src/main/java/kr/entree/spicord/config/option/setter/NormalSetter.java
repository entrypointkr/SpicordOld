package kr.entree.spicord.config.option.setter;

import kr.entree.spicord.config.option.ConfigSetter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NormalSetter<T> implements ConfigSetter<T> {
    private final String key;

    public NormalSetter(String key) {
        this.key = key;
    }

    @Override
    public void set(ConfigurationSection config, T value) {
        config.set(key, value);
    }
}
