package kr.entree.spicord.bukkit.util;

import io.vavr.control.Option;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class ConfigurationSections {
    public Option<Object> get(ConfigurationSection section, String key) {
        return Option.of(section.get(key));
    }

    @SuppressWarnings("unchecked")
    public Option<Collection<Object>> getCollection(ConfigurationSection section, String key) {
        return get(section, key).map(value ->
                value instanceof Collection
                        ? new ArrayList<>(((Collection<Object>) value))
                        : Collections.singletonList(value)
        );
    }

    public Option<Collection<String>> getStringCollection(ConfigurationSection section, String key) {
        val collection = getCollection(section, key);
        return collection.map(c -> c.stream().map(Object::toString).collect(Collectors.toList()));
    }

    public Option<ConfigurationSection> getSection(ConfigurationSection section, String key) {
        return Option.of(section.getConfigurationSection(key));
    }

    public <T> Option<T> get(ConfigurationSection section, String key, Class<T> type) {
        return get(section, key)
                .filter(type::isInstance)
                .map(type::cast);
    }
}
