package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.util.ConfigurationSections;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2020-04-02
 */
@Data
public class CommandData {
    private final DiscordCommand playerListCommand;

    public static CommandData parse(ConfigurationSection section, SpicordConfig config) {
        return new CommandData(
                DiscordCommand.parse(ConfigurationSections.getSection(section, "players"), config)
        );
    }
}
