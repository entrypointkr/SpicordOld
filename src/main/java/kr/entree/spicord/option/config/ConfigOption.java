package kr.entree.spicord.option.config;

import kr.entree.spicord.option.Option;
import kr.entree.spicord.option.config.getter.BooleanGetter;
import kr.entree.spicord.option.config.getter.NumberGetter;
import kr.entree.spicord.option.config.setter.NormalSetter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class ConfigOption<T> implements Option<T> {
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

    @Override
    public T get() {
        return getter.get(section);
    }

    @Override
    public void set(T value) {
        setter.set(section, value);
    }
}
