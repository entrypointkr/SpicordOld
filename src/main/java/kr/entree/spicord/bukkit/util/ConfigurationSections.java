package kr.entree.spicord.bukkit.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ConfigurationSections {
    public Optional<Collection<?>> getCollection(ConfigurationSection section, String key) {
        val object = section.get(key);
        if (object instanceof Collection) {
            return Optional.of(((Collection<?>) object));
        }
        return Optional.ofNullable(object).map(Collections::singletonList);
    }

    public Optional<Collection<String>> getStringCollection(ConfigurationSection section, String key) {
        val collection = getCollection(section, key);
        return collection.map(c -> c.stream().map(Object::toString).collect(Collectors.toList()));
    }

    public ConfigurationSection getSection(ConfigurationSection section, String key) {
        val ret = section.getConfigurationSection(key);
        return ret != null ? ret : new MemoryConfiguration();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigurationSection section, String key, T defaultValue) {
        val type = defaultValue.getClass();
        val value = section.get(key);
        return type.isInstance(value) ? (T) value : defaultValue;
    }
}
