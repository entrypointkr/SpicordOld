package kr.entree.spicord;

import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.JDAHandler;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class ConfigTest {
    @Test
    public void parse() throws IOException, InvalidConfigurationException {
        InputStream in = getClass().getResourceAsStream("/config.yml");
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        SpicordConfig config = new SpicordConfig(yamlConfig, mock(Plugin.class));
        JDAHandler message = config.getSendMessage("messages.player-ban", Parameter.of());
        System.out.println(message);
    }
}
