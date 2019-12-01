package kr.entree.spicord.config.option;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public interface ConfigSetter<T> {
    void set(ConfigurationSection config, T value);
}
