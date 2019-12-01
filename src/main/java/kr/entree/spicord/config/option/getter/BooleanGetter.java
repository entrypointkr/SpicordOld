package kr.entree.spicord.config.option.getter;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class BooleanGetter extends AssociativeGetter<Boolean> {
    public BooleanGetter(String key) {
        super(key);
    }

    @Override
    public Boolean get(ConfigurationSection config, String key) {
        return config.getBoolean(key);
    }
}
