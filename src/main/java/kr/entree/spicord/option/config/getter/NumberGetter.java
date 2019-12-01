package kr.entree.spicord.option.config.getter;

import lombok.val;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NumberGetter extends AssociativeGetter<Number> {
    public NumberGetter(String key) {
        super(key);
    }

    @Override
    public Number get(ConfigurationSection config, String key) {
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
