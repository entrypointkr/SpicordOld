package kr.entree.spicord.option.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public interface ConfigGetter<T> {
    T get(ConfigurationSection config);
}
