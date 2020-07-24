package kr.entree.spicord.property.config;

import kr.entree.spicord.property.Property;
import kr.entree.spicord.property.config.getter.BooleanGetter;
import kr.entree.spicord.property.config.getter.NumberGetter;
import kr.entree.spicord.property.config.setter.NormalSetter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class ConfigProperty<T> implements Property<T> {
    private final ConfigurationSection section;
    private final ConfigGetter<T> getter;
    private final ConfigSetter<T> setter;

    private ConfigProperty(ConfigurationSection section, ConfigGetter<T> getter, ConfigSetter<T> setter) {
        this.section = section;
        this.getter = getter;
        this.setter = setter;
    }

    public static <T> ConfigProperty<T> of(ConfigurationSection section, ConfigGetter<T> getter, ConfigSetter<T> setter) {
        return new ConfigProperty<>(section, getter, setter);
    }

    public static <T> ConfigProperty<T> of(ConfigurationSection section, ConfigGetter<T> getter, String key) {
        return of(section, getter, new NormalSetter<>(key));
    }

    public static ConfigProperty<Number> ofNumber(ConfigurationSection section, String key) {
        return of(section, new NumberGetter(key), key);
    }

    public static ConfigProperty<Boolean> ofBoolean(ConfigurationSection section, String key) {
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
