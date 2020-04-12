package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.util.ConfigurationSections;
import lombok.experimental.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JunHyung Lim on 2019-11-26
 */
public abstract class PluginConfiguration implements ConfigurationSection {
    @Delegate(types = ConfigurationSection.class)
    private final YamlConfiguration config;
    private final Plugin plugin;

    public PluginConfiguration(YamlConfiguration config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    public PluginConfiguration(Plugin plugin) {
        this(new YamlConfiguration(), plugin);
    }

    protected abstract String getFileName();

    public File createFile(Plugin plugin) {
        return new File(plugin.getDataFolder(), getFileName());
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public ConfigurationSection getSectionOrEmpty(String key) {
        return ConfigurationSections.getSection(this, key);
    }

    public static String readText(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)
                .forEach(line -> builder.append(line).append('\n'));
        return builder.toString();
    }

    public static void writeText(File file, String contents) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(contents);
        }
    }

    private void write(File file, String contents) {
        try {
            writeText(file, contents);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> "Failed while saving: " + file);
        }
    }

    public void load() {
        File file = createFile(plugin);
        try {
            config.loadFromString(readText(file));
        } catch (IOException e) {
            // Ignore
        } catch (InvalidConfigurationException e) {
            getLogger().log(Level.WARNING, e, () -> "Failed while loading: " + file);
        }
    }

    public void save() {
        File file = createFile(plugin);
        write(file, config.saveToString());
    }

    public void saveAsync() {
        File file = createFile(plugin);
        String contents = config.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(file, contents));
    }
}
