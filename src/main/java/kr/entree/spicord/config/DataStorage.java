package kr.entree.spicord.config;

import dagger.Reusable;
import kr.entree.spicord.option.config.ConfigOption;
import kr.entree.spicord.option.config.getter.NumberGetter;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
@Reusable
public class DataStorage extends PluginConfiguration {
    @Inject
    public DataStorage(Plugin plugin) {
        super(plugin, "data.yml");
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
}
