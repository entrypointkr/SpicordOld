package kr.entree.spicord.bukkit.util;

import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ConfigurationSections {
    public static Collection<?> getCollection(ConfigurationSection section, String key) {
        val object = section.get(key);
        if (object instanceof Collection) {
            return ((Collection<?>) object);
        }
        return object != null ? Collections.singletonList(object) : Collections.emptyList();
    }

    public static Collection<String> getStringCollection(ConfigurationSection section, String key) {
        val collection = getCollection(section, key);
        return collection.stream().map(Object::toString).collect(Collectors.toList());
    }

    public static ConfigurationSection getSection(ConfigurationSection section, String key) {
        val ret = section.getConfigurationSection(key);
        return ret != null ? ret : new MemoryConfiguration();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(ConfigurationSection section, String key, T defaultValue) {
        val type = defaultValue.getClass();
        val value = section.get(key);
        return type.isInstance(value) ? (T) value : defaultValue;
    }
}
