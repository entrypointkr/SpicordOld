package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.event.GuildJoinEvent;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.supplier.PrivateChannelOpener;
import kr.entree.spicord.discord.task.ChannelHandler;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static kr.entree.spicord.config.SpicordConfig.featureKey;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class DiscordToDiscord implements Listener {
    private final SpicordConfig config;

    public DiscordToDiscord(SpicordConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onGuildJoin(GuildJoinEvent e) {
        if (!config.isEnabled(featureKey("welcome"))) {
            return;
        }
        if (e.getUser().isBot()) {
            return;
        }
        val parameter = new Parameter().put(e.getUser());
        e.getDiscord().addTask(new ChannelHandler<>(
                PrivateChannelOpener.of(e.getUser().getId()),
                config.getMessage("welcome", parameter)
        ));
    }
}
