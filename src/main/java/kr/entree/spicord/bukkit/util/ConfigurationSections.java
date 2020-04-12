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
}
