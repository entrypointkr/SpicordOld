package kr.entree.spicord.config.option.getter;

import kr.entree.spicord.config.option.ConfigGetter;
import lombok.val;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NumberGetter implements ConfigGetter<Number> {
    private final String key;

    public NumberGetter(String key) {
        this.key = key;
    }

    @Override
    public Number get(ConfigurationSection config) {
        val value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value);
        } else if (value != null) {
            try {
                return NumberUtils.createNumber(value.toString());
            } catch (Exception ex) {
                // Ignore
            }
        }
        return -1;
    }
}
