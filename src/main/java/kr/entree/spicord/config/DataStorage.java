package kr.entree.spicord.config;

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

    public long getPlayerChatWebhookId() {
        return getLong("webhook.chat", -1);
    }

    @Override
    protected String getFileName() {
        return "data.yml";
    }
}
