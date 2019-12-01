package kr.entree.spicord.config.option;

import kr.entree.spicord.config.option.getter.BooleanGetter;
import kr.entree.spicord.config.option.getter.NumberGetter;
import kr.entree.spicord.config.option.setter.NormalSetter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class ConfigOption<T> {
    private final ConfigurationSection section;
    private final ConfigGetter<T> getter;
    private final ConfigSetter<T> setter;

    private ConfigOption(ConfigurationSection section, ConfigGetter<T> getter, ConfigSetter<T> setter) {
        this.section = section;
        this.getter = getter;
        this.setter = setter;
    }

    public static <T> ConfigOption<T> of(ConfigurationSection section, ConfigGetter<T> getter, ConfigSetter<T> setter) {
        return new ConfigOption<>(section, getter, setter);
    }

    public static <T> ConfigOption<T> of(ConfigurationSection section, ConfigGetter<T> getter, String key) {
        return of(section, getter, new NormalSetter<>(key));
    }

    public static ConfigOption<Number> ofNumber(ConfigurationSection section, String key) {
        return of(section, new NumberGetter(key), key);
    }

    public static ConfigOption<Boolean> ofBoolean(ConfigurationSection section, String key) {
        return of(section, new BooleanGetter(key), key);
    }

    public T get() {
        return getter.get(section);
    }

    public void set(T value) {
        setter.set(section, value);
    }
}
