package kr.entree.spicord.bukkit.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

@RequiredArgsConstructor
public class Cache<T> {
    private final ConfigurationSection section;
    private final Function<ConfigurationSection, T> getter;
    private T value;

    public T get() {
        if (value == null) {
            reload();
        }
        return value;
    }

    public void clear() {
        value = null;
    }

    public void reload() {
        value = getter.apply(section);
    }
}
