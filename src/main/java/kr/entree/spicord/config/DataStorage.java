package kr.entree.spicord.config;

import kr.entree.spicord.option.config.ConfigOption;
import kr.entree.spicord.option.config.getter.NumberGetter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class DataStorage extends PluginConfiguration {
    public DataStorage(YamlConfiguration config, Plugin plugin) {
        super(config, plugin);
    }

    public DataStorage(Plugin plugin) {
        super(plugin);
    }

    public ConfigOption<Number> getPlayerChatWebhookId() {
        String key = "webhook-id";
        return ConfigOption.of(
                this,
                new NumberGetter(key),
                (config, value) -> {
                    if (!value.equals(config.get(key))) {
                        config.set(key, value);
                        saveAsync();
                    }
                }
        );
    }

    @Override
    protected String getFileName() {
        return "data.yml";
    }
}
