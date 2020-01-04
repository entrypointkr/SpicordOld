package kr.entree.spicord;

import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.task.channel.handler.MessageChannelHandler;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class ConfigTest {
    public static SpicordConfig createConfig(Plugin plugin) throws IOException, InvalidConfigurationException {
        InputStream in = ConfigTest.class.getResourceAsStream("/config.yml");
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return new SpicordConfig(yamlConfig, plugin);
    }

    @Test
    public void parse() throws IOException, InvalidConfigurationException {
        Plugin mockPlugin = mock(Plugin.class);
        Player player = BukkitFactory.createPlayer("EntryPoint");
        SpicordConfig config = createConfig(mockPlugin);
        LangConfig langConfig = new LangConfig(mockPlugin);
        config.getChannelTask("player-kick", new Parameter());
        Assert.assertEquals(
                Lang.colorize(Lang.VERIFY_MESSAGE.getDef()
                        .replace("%prefix%", Lang.PREFIX.getDef())
                        .replace("%discord%", "DiscordName")
                        .replace("%code%", "1234")),
                langConfig.format(
                        Lang.VERIFY_MESSAGE,
                        new Parameter().put(player)
                                .put("%discord%", "DiscordName")
                                .put("%code%", 1234)
                )
        );
    }

    private static ConfigurationSection createEmbedSection() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("title", "test");
        return config;
    }

    @Test
    public void message() {
        Plugin plugin = mock(Plugin.class);
        YamlConfiguration config = new YamlConfiguration();
        SpicordConfig spicordConfig = new SpicordConfig(config, plugin);
        config.set("messages.welcome", Arrays.asList(
                createEmbedSection(),
                "test",
                createEmbedSection(),
                "a",
                "b",
                createEmbedSection(),
                "c",
                "d"
        ));
        AtomicInteger counter = new AtomicInteger();
        MessageChannelHandler message = spicordConfig.getMessage("welcome", new Parameter());
        message.handle(JDAFactory.createTextChannel(msg -> counter.incrementAndGet()));
        Assert.assertEquals(6, counter.get());
    }
}
