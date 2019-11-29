package kr.entree.spicord;

import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.JDAHandler;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Assert;
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
        Plugin mockPlugin = mock(Plugin.class);
        Player player = BukkitFactory.createPlayer("EntryPoint");
        InputStream in = getClass().getResourceAsStream("/config.yml");
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        SpicordConfig config = new SpicordConfig(yamlConfig, mockPlugin);
        LangConfig langConfig = new LangConfig(mockPlugin);
        JDAHandler message = config.getSendMessage("messages.player-ban", Parameter.of());
        System.out.println(message);
        Assert.assertEquals(
                Lang.colorize(Lang.VERIFY_MESSAGE.getDef()
                        .replace("%prefix%", Lang.PREFIX.getDef())
                        .replace("%discord%", "DiscordName")
                        .replace("%code%", "1234")),
                langConfig.format(
                        Lang.VERIFY_MESSAGE,
                        Parameter.ofPlayer(player).put("%discord%", "DiscordName")
                                .put("%code%", 1234)
                )
        );
    }
}
