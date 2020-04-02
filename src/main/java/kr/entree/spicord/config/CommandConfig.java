package kr.entree.spicord.config;

import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * Created by JunHyung Lim on 2020-04-02
 */
public class CommandConfig {
    private final ConfigurationSection config;
    private final SpicordConfig top;

    public CommandConfig(ConfigurationSection config, SpicordConfig top) {
        this.config = config;
        this.top = top;
    }

    public DiscordCommand getPlayerList() {
        return DiscordCommand.parse(getSection("players"), top);
    }

    private ConfigurationSection getSection(String key) {
        val section = config.getConfigurationSection(key);
        return section != null ? section : new MemoryConfiguration();
    }
}
