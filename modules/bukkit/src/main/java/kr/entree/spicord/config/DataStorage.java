package kr.entree.spicord.config;

import dagger.Reusable;
import kr.entree.spicord.property.config.ConfigProperty;
import kr.entree.spicord.property.config.getter.NumberGetter;
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

    public ConfigProperty<Number> getPlayerChatWebhookId() {
        String key = "webhook-id";
        return ConfigProperty.of(
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
